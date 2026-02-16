import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

import pandas as pd
from datetime import datetime
import json
from typing import Dict
from database import DatabaseManager
from config import Config

# Check disponibilità AI
try:
    from llama_cpp import Llama
    AI_AVAILABLE = True
    print("✅ llama-cpp-python disponibile!")
except ImportError:
    AI_AVAILABLE = False
    print("⚠️  llama-cpp-python non installato - Modalità base")

class AIModelManager:
    """Gestisce il modello AI (Llama/Mistral)"""
    def __init__(self):
        if not AI_AVAILABLE:
            self.model = None
            return

        model_path = Config.MODEL_PATH
        if not os.path.exists(model_path):
            print(f"⚠️  Modello non trovato: {model_path}")
            print("   Scarica un modello GGUF e mettilo nella cartella models/")
            self.model = None
            return

        print(f"🔄 Caricamento modello AI: {os.path.basename(model_path)}")
        try:
            self.model = Llama(
                model_path=model_path,
                n_ctx=2048,
                n_threads=Config.AI_N_THREADS,
                n_gpu_layers=0  # CPU only
            )
            print("✅ Modello AI caricato con successo!")
        except Exception as e:
            print(f"❌ Errore caricamento modello: {e}")
            self.model = None

    def generate_ai_explanation(self, product_data: Dict) -> str:
        """Genera spiegazione usando AI vera"""
        if not self.model:
            return None

        prompt = f"""Sei un esperto di inventory management. Analizza questo prodotto e fornisci una raccomandazione d'acquisto professionale in italiano:

Prodotto: {product_data['product_name']}
Stock attuale: {product_data['current_stock']} unità
Vendite totali: {product_data['total_sold']} unità
Velocità vendita: {product_data['daily_sales_rate']} unità/giorno
Giorni di stock rimanenti: {product_data['days_of_stock']} giorni
Revenue totale: €{product_data['total_revenue']:.2f}
Prezzo unitario: €{product_data['price']:.2f}

Fornisci un'analisi con:
1. Valutazione della situazione attuale
2. Livello di urgenza e motivazione
3. Quantità consigliata da ordinare
4. Insight strategici sul prodotto

Sii conciso e professionale (max 200 parole)."""

        try:
            response = self.model(
                prompt,
                max_tokens=400,
                temperature=0.7,
                stop=["</s>", "\n\n\n", "---"]
            )
            return response['choices'][0]['text'].strip()
        except Exception as e:
            print(f"⚠️  Errore generazione AI: {e}")
            return None


class PurchaseRecommendationAI:
    def __init__(self):
        self.db_manager = DatabaseManager()

        # Inizializza AI se disponibile
        self.ai_model = AIModelManager() if AI_AVAILABLE else None

        if self.ai_model and self.ai_model.model:
            print("🤖 Analyzer con AI ibrido attivo (AI solo per casi critici)!")
        else:
            print("📊 Analyzer in modalità logica veloce")

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
        """Genera spiegazione (AI solo per casi critici, altrimenti logica veloce)"""

        name = product['product_name']
        stock = product['current_stock']
        sold = product['total_sold']
        rate = product['daily_sales_rate']
        days = product['days_of_stock']
        revenue = product['total_revenue']
        urgency = product['urgency']
        suggested = product['suggested_order_quantity']
        price = product.get('price', 0)

        # Determina se il caso richiede analisi AI approfondita
        needs_ai_analysis = False
        ai_reason = ""

        if self.ai_model and self.ai_model.model:
            # Usa AI solo per:
            # 1. Urgenze CRITICHE
            if urgency == "ALTA":
                needs_ai_analysis = True
                ai_reason = "urgenza critica"

            # 2. Prodotti premium (>500€)
            elif price > 500:
                needs_ai_analysis = True
                ai_reason = "prodotto premium"

            # 3. Best sellers con problemi di stock
            elif rate > 3 and days < 10:
                needs_ai_analysis = True
                ai_reason = "best seller con stock critico"

            # 4. Slow movers con stock eccessivo
            elif rate < 0.5 and stock > 20:
                needs_ai_analysis = True
                ai_reason = "slow mover con eccesso"

            # 5. Revenue molto alto
            elif revenue > 5000:
                needs_ai_analysis = True
                ai_reason = "alto valore revenue"

        # Se richiede AI, prova a generare con il modello
        if needs_ai_analysis:
            print(f"   🤖 Analisi AI per '{name[:40]}' ({ai_reason})...")
            ai_response = self.ai_model.generate_ai_explanation(product)
            if ai_response:
                return f"🤖 ANALISI AI APPROFONDITA ({ai_reason.upper()}):\n\n{ai_response}\n\n{'─'*60}\n✨ Generato da AI per caso particolare"

        # Logica veloce per tutti gli altri casi
        explanation = []

        # Header compatto
        explanation.append(f"📊 {name}")
        explanation.append(f"Stock: {stock} unità | Vendite: {sold} | Velocità: {rate}/giorno | Durata: {days:.1f}gg | Revenue: €{revenue:.2f}")

        # Diagnosi rapida
        if urgency == "ALTA":
            explanation.append(f"\n🔴 URGENZA CRITICA: Stock critico ({days:.1f} giorni). Ordina subito {suggested} unità.")
            if rate > 2:
                explanation.append(f"   Alta rotazione ({rate}/giorno) - Priorità massima!")

        elif urgency == "MEDIA":
            explanation.append(f"\n🟡 ATTENZIONE: Stock per {days:.1f} giorni. Ordina {suggested} unità entro 2 settimane.")
            if rate > 1:
                explanation.append(f"   Buona rotazione - Monitora regolarmente.")

        elif urgency == "BASSA":
            explanation.append(f"\n🟢 OK: Stock sufficiente ({days:.1f} giorni). Nessuna urgenza.")

        else:
            explanation.append(f"\n⚪ ECCESSO: Stock per {days:.1f}+ giorni. Non ordinare.")

        # Raccomandazione economica
        if suggested > 0:
            investment = suggested * price
            expected_return = investment * 1.3
            explanation.append(f"\n💰 Investimento: €{investment:.2f} → Revenue atteso: €{expected_return:.2f}")

        # Insight rapidi
        insights = []
        avg_price = revenue / sold if sold > 0 else 0

        if avg_price > 100:
            insights.append("💎 Premium")
        if rate > 3:
            insights.append("⚡ Best Seller")
        if rate < 0.5 and stock > 20:
            insights.append("⚠️ Slow Mover")

        if insights:
            explanation.append(f"\n{' | '.join(insights)}")

        explanation.append(f"\n{'─'*60}\n⚡ Analisi logica veloce")

        return "\n".join(explanation)

    def calculate_metrics(self, sales_df: pd.DataFrame) -> Dict:
        """Calcola metriche di business per ogni prodotto"""
        if sales_df.empty:
            return {
                'products': [],
                'analysis_date': datetime.now().isoformat(),
                'total_products_analyzed': 0,
                'ai_enabled': bool(self.ai_model and self.ai_model.model),
                'ai_analyses_performed': 0,
                'note': 'Nessun dato disponibile per analisi'
            }

        metrics = []
        ai_count = 0  # Conta quante volte si usa AI

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

            # Conta se ha usato AI
            if explanation.startswith("🤖 ANALISI AI"):
                ai_count += 1

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
                'explanation': explanation,
                'analyzed_with_ai': explanation.startswith("🤖 ANALISI AI")
            })

        return {
            'products': metrics,
            'analysis_date': datetime.now().isoformat(),
            'total_products_analyzed': len(metrics),
            'ai_enabled': bool(self.ai_model and self.ai_model.model),
            'ai_analyses_performed': ai_count,
            'logic_analyses_performed': len(metrics) - ai_count,
            'ai_mode': 'hybrid' if (self.ai_model and self.ai_model.model) else 'logic',
            'efficiency_percent': round(((len(metrics) - ai_count) / len(metrics) * 100), 1) if len(metrics) > 0 else 0,
            'note': f'Analisi ibrida: {ai_count} AI approfondite + {len(metrics)-ai_count} logica veloce' if ai_count > 0 else 'Analisi logica veloce per tutti i prodotti'
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

        print(f"✅ Analisi completata! Analizzati {metrics['total_products_analyzed']} prodotti")
        if metrics['ai_enabled']:
            print(f"   🤖 Analisi AI: {metrics['ai_analyses_performed']} prodotti")
            print(f"   ⚡ Logica veloce: {metrics['logic_analyses_performed']} prodotti")
            print(f"   📊 Efficienza: {metrics['efficiency_percent']}% risparmiato\n")
        else:
            print(f"   Modalità: Logica veloce\n")

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

            # Mostra tutti i prodotti con spiegazioni complete
            for i, p in enumerate(results['products'], 1):
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
                    ai_marker = "🤖" if p['analyzed_with_ai'] else "⚡"
                    print(f"   {ai_marker} {p['product_name']:<40} → Ordina {p['suggested_order_quantity']} unità")

            if medium:
                print(f"\n🟡 ATTENZIONE MEDIA ({len(medium)} prodotti):")
                for p in medium:
                    ai_marker = "🤖" if p['analyzed_with_ai'] else "⚡"
                    print(f"   {ai_marker} {p['product_name']:<40} → Ordina {p['suggested_order_quantity']} unità")

            # Calcolo investimento totale
            total_investment = sum(p['suggested_order_quantity'] * p['price']
                                   for p in results['products'] if p['suggested_order_quantity'] > 0)
            print(f"\n💰 INVESTIMENTO TOTALE CONSIGLIATO: €{total_investment:,.2f}")

            # Statistiche analisi
            print(f"\n📊 STATISTICHE ANALISI:")
            print(f"   Prodotti analizzati: {results['total_products_analyzed']}")
            if results['ai_enabled']:
                print(f"   🤖 Analisi AI approfondite: {results['ai_analyses_performed']}")
                print(f"   ⚡ Analisi logica veloce: {results['logic_analyses_performed']}")
                print(f"   📈 Efficienza: {results['efficiency_percent']}% computazione risparmiata")
                print(f"   🎯 Modalità: {results['ai_mode'].upper()}")
            else:
                print(f"   Modalità: LOGIC (AI non disponibile)")

            print(f"\n💡 {results['note']}")

        else:
            print("⚠️  Nessun prodotto analizzato")

    except Exception as e:
        print(f"\n❌ ERRORE: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
