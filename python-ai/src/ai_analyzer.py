import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

import pandas as pd
from datetime import datetime
import json
from typing import Dict
from database import DatabaseManager
from config import Config

class PurchaseRecommendationAI:
    def __init__(self):
        self.db_manager = DatabaseManager()
        print("✅ Analyzer inizializzato (modalità base - senza AI)")

    def extract_sales_data(self) -> pd.DataFrame:
        """Estrae dati vendite dal database"""
        conn = self.db_manager.get_connection()

        query = """
                SELECT
                    a.codice as product_id,
                    a.descrizione as product_name,
                    COALESCE(a.gruppo, 'N/A') as category,
                    COALESCE(a.prezzodilistino, 0) as price,
                    COALESCE(ast.quantita_stock, 0) as current_stock,
                    COUNT(oi.id) as total_orders,
                    COALESCE(SUM(oi.quantita), 0) as total_quantity_sold,
                    COALESCE(AVG(oi.quantita), 0) as avg_quantity_per_order,
                    COALESCE(SUM(oi.subtotale), 0) as total_revenue,
                    MIN(o.data_ordine) as first_sale_date,
                    MAX(o.data_ordine) as last_sale_date
                FROM articoli a
                         LEFT JOIN articolo_stock ast ON a.codice = ast.codice_articolo
                         LEFT JOIN ordini_items oi ON a.codice = oi.codice_articolo
                         LEFT JOIN ordini o ON oi.ordine_id = o.id
                GROUP BY a.codice, a.descrizione, a.gruppo, a.prezzodilistino, ast.quantita_stock
                ORDER BY total_quantity_sold DESC NULLS LAST
                LIMIT 20; \
                """

        try:
            with conn.cursor() as cur:
                cur.execute(query)
                rows = cur.fetchall()
                if not rows:
                    print("⚠️  Nessun dato trovato!")
                    return pd.DataFrame()
                df = pd.DataFrame(rows)
            conn.close()
            print(f"📊 Estratti {len(df)} prodotti dal database")
            return df
        except Exception as e:
            print(f"❌ Errore estrazione dati: {e}")
            import traceback
            traceback.print_exc()
            conn.close()
            raise

    def generate_explanation(self, product: Dict) -> str:
        """Genera spiegazione dettagliata per la raccomandazione"""
        name = product['product_name']
        stock = product['current_stock']
        sold = product['total_sold']
        rate = product['daily_sales_rate']
        days = product['days_of_stock']
        revenue = product['total_revenue']
        urgency = product['urgency']
        suggested = product['suggested_order_quantity']

        explanation = []

        # Analisi situazione attuale
        explanation.append(f"📊 ANALISI '{name}':")
        explanation.append(f"   Stock attuale: {stock} unità - Vendite totali: {sold} unità")
        explanation.append(f"   Velocità media: {rate} unità/giorno - Revenue: €{revenue:.2f}")

        # Diagnosi urgenza
        if urgency == "ALTA":
            explanation.append(f"\n🔴 URGENZA CRITICA:")
            explanation.append(f"   Il tuo stock durerà solo {days:.1f} giorni al ritmo attuale!")
            explanation.append(f"   Rischio rottura stock entro questa settimana.")
            if rate > 2:
                explanation.append(f"   Prodotto ad alta rotazione ({rate} unità/giorno) - Priorità massima.")

        elif urgency == "MEDIA":
            explanation.append(f"\n🟡 ATTENZIONE RICHIESTA:")
            explanation.append(f"   Stock sufficiente per {days:.1f} giorni.")
            explanation.append(f"   Considera di ordinare entro 2 settimane per evitare rotture.")
            if rate > 1:
                explanation.append(f"   Buona rotazione ({rate} unità/giorno) - Monitora regolarmente.")

        elif urgency == "BASSA":
            explanation.append(f"\n🟢 SITUAZIONE SOTTO CONTROLLO:")
            explanation.append(f"   Stock sufficiente per {days:.1f} giorni.")
            explanation.append(f"   Nessuna urgenza immediata, monitora il trend.")

        else:  # NESSUNA
            explanation.append(f"\n⚪ STOCK ECCESSIVO:")
            explanation.append(f"   Hai scorte per oltre {days:.1f} giorni.")
            explanation.append(f"   Non ordinare - Rischio di invenduto e costi di stoccaggio.")

        # Raccomandazione specifica
        if suggested > 0:
            explanation.append(f"\n💡 RACCOMANDAZIONE:")
            explanation.append(f"   Ordina {suggested} unità per coprire 30 giorni di vendite.")
            explanation.append(f"   Questo ti proteggerà da eventuali picchi di domanda.")

            # Calcolo investimento
            if 'price' in product and product['price'] > 0:
                investment = suggested * product['price']
                expected_revenue = suggested * product['price'] * 1.3  # Margine stimato 30%
                explanation.append(f"   Investimento necessario: €{investment:.2f}")
                explanation.append(f"   Revenue atteso (con margine 30%): €{expected_revenue:.2f}")
        else:
            explanation.append(f"\n✋ SUGGERIMENTO:")
            explanation.append(f"   Non ordinare ora. Attendi che lo stock scenda sotto le {int(stock * 0.7)} unità.")

        # Insight aggiuntivi
        if sold > 0:
            avg_price = revenue / sold if sold > 0 else 0
            if avg_price > 100:  # Prodotto di valore
                explanation.append(f"\n💎 NOTA: Prodotto premium (€{avg_price:.2f}/unità) - Priorità alta anche con stock.")

            if rate > 3:
                explanation.append(f"\n⚡ BEST SELLER: Rotazione eccezionale! Considera aumento scorta di sicurezza.")

            if rate < 0.5 and stock > 20:
                explanation.append(f"\n⚠️  SLOW MOVER: Bassa rotazione. Valuta promozioni o sconti per smaltire stock.")

        return "\n".join(explanation)

    def calculate_metrics(self, sales_df: pd.DataFrame) -> Dict:
        """Calcola metriche di business per ogni prodotto"""
        if sales_df.empty:
            return {
                'products': [],
                'analysis_date': datetime.now().isoformat(),
                'total_products_analyzed': 0,
                'ai_enabled': False,
                'note': 'Nessun dato disponibile per analisi'
            }

        metrics = []

        for _, product in sales_df.iterrows():
            product_id = product['product_id']
            total_sold = float(product['total_quantity_sold'])
            current_stock = int(product['current_stock'])

            # Calcola velocità di vendita
            if pd.notna(product['last_sale_date']) and pd.notna(product['first_sale_date']):
                days_active = (pd.to_datetime(product['last_sale_date']) -
                               pd.to_datetime(product['first_sale_date'])).days
                days_active = max(days_active, 1)
            else:
                days_active = 1

            daily_sales_rate = total_sold / days_active if days_active > 0 else 0
            days_of_stock = current_stock / daily_sales_rate if daily_sales_rate > 0 else 999

            # Determina status stock
            if days_of_stock < 7:
                stock_status = "CRITICO"
                urgency = "ALTA"
                suggested_order = int(daily_sales_rate * 30)
            elif days_of_stock < 30:
                stock_status = "BASSO"
                urgency = "MEDIA"
                suggested_order = int(daily_sales_rate * 20)
            elif days_of_stock < 90:
                stock_status = "NORMALE"
                urgency = "BASSA"
                suggested_order = 0
            else:
                stock_status = "ECCESSO"
                urgency = "NESSUNA"
                suggested_order = 0

            # Prepara dati per spiegazione
            product_data = {
                'product_name': product['product_name'],
                'current_stock': current_stock,
                'total_sold': int(total_sold),
                'daily_sales_rate': round(daily_sales_rate, 2),
                'days_of_stock': round(days_of_stock, 1),
                'total_revenue': float(product['total_revenue']),
                'urgency': urgency,
                'suggested_order_quantity': suggested_order,
                'price': float(product['price'])
            }

            # Genera spiegazione
            explanation = self.generate_explanation(product_data)

            metrics.append({
                'product_id': str(product_id),
                'product_name': product['product_name'],
                'category': product['category'],
                'price': float(product['price']),
                'current_stock': current_stock,
                'total_sold': int(total_sold),
                'total_revenue': float(product['total_revenue']),
                'daily_sales_rate': round(daily_sales_rate, 2),
                'days_of_stock': round(days_of_stock, 1),
                'stock_status': stock_status,
                'urgency': urgency,
                'suggested_order_quantity': suggested_order,
                'recommendation': f"Ordinare {suggested_order} unità - Urgenza: {urgency}",
                'explanation': explanation
            })

        return {
            'products': metrics,
            'analysis_date': datetime.now().isoformat(),
            'total_products_analyzed': len(metrics),
            'ai_enabled': False,
            'note': 'Analisi basata su metriche avanzate con spiegazioni dettagliate.'
        }

    def run_analysis(self) -> Dict:
        """Esegue analisi completa"""
        print("\n" + "="*70)
        print("🚀 AVVIO ANALISI PURCHASE RECOMMENDATION")
        print("="*70 + "\n")

        print("📥 Estrazione dati vendite...")
        sales_df = self.extract_sales_data()

        print("🔢 Calcolo metriche e generazione spiegazioni...")
        metrics = self.calculate_metrics(sales_df)

        print(f"✅ Analisi completata! Analizzati {metrics['total_products_analyzed']} prodotti\n")

        return metrics

def main():
    """Test standalone"""
    try:
        analyzer = PurchaseRecommendationAI()
        results = analyzer.run_analysis()

        # Salva output
        output_dir = os.path.join(os.path.dirname(__file__), '..', 'output')
        os.makedirs(output_dir, exist_ok=True)
        output_file = os.path.join(output_dir, f"recommendations_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json")

        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(results, f, indent=2, ensure_ascii=False)

        print(f"💾 Risultati salvati: {output_file}\n")

        # Mostra risultati con spiegazioni
        if results['products']:
            print("📊 ANALISI DETTAGLIATA CON SPIEGAZIONI:\n")
            print("="*80)

            # Mostra top 5 prodotti con spiegazioni complete
            for i, p in enumerate(results['products'][:5], 1):
                print(f"\n{'='*80}")
                print(f"PRODOTTO #{i}")
                print('='*80)
                print(p['explanation'])
                print('='*80)

            # Riepilogo generale
            print(f"\n\n📋 RIEPILOGO GENERALE:")
            print(f"{'='*80}")

            critical = [p for p in results['products'] if p['urgency'] == 'ALTA']
            medium = [p for p in results['products'] if p['urgency'] == 'MEDIA']

            if critical:
                print(f"\n🔴 URGENZE CRITICHE ({len(critical)} prodotti):")
                for p in critical:
                    print(f"   • {p['product_name']:<40} → Ordina {p['suggested_order_quantity']} unità")

            if medium:
                print(f"\n🟡 ATTENZIONE MEDIA ({len(medium)} prodotti):")
                for p in medium:
                    print(f"   • {p['product_name']:<40} → Ordina {p['suggested_order_quantity']} unità")

            # Calcolo investimento totale
            total_investment = sum(p['suggested_order_quantity'] * p['price']
                                   for p in results['products'] if p['suggested_order_quantity'] > 0)
            print(f"\n💰 INVESTIMENTO TOTALE CONSIGLIATO: €{total_investment:,.2f}")

        else:
            print("⚠️  Nessun prodotto analizzato")

    except Exception as e:
        print(f"\n❌ ERRORE: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
