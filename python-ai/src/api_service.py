import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from flask import Flask, jsonify
from flask_cors import CORS
from ai_analyzer import PurchaseRecommendationAI
from database import DatabaseManager
from config import Config

app = Flask(__name__)
CORS(app)

# Inizializza analyzer
print("🔄 Inizializzazione servizio AI...")
analyzer = None

try:
    analyzer = PurchaseRecommendationAI()
    print("✅ Servizio AI pronto!")
except Exception as e:
    print(f"❌ Errore inizializzazione: {e}")

@app.route('/api/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    db_manager = DatabaseManager()
    db_healthy = db_manager.test_connection()

    ai_status = "not_loaded"
    if analyzer:
        if analyzer.ai_model and analyzer.ai_model.model:
            ai_status = "hybrid"  # AI per casi critici
        else:
            ai_status = "logic"  # Solo logica

    return jsonify({
        'status': 'healthy' if analyzer and db_healthy else 'unhealthy',
        'service': 'AI Recommendation Engine',
        'analyzer_loaded': analyzer is not None,
        'database_connected': db_healthy,
        'ai_mode': ai_status,
        'description': 'AI approfondita per casi critici + logica veloce per resto' if ai_status == 'hybrid' else 'Logica veloce'
    }), 200 if (analyzer and db_healthy) else 503

@app.route('/api/recommendations', methods=['GET'])
def get_recommendations():
    """Genera raccomandazioni"""
    if not analyzer:
        return jsonify({
            'error': 'AI service not initialized'
        }), 503

    try:
        print("\n📞 Richiesta raccomandazioni ricevuta...")
        results = analyzer.run_analysis()
        return jsonify(results), 200
    except Exception as e:
        print(f"❌ Errore: {e}")
        import traceback
        traceback.print_exc()
        return jsonify({
            'error': str(e),
            'type': type(e).__name__
        }), 500

@app.route('/api/test-db', methods=['GET'])
def test_database():
    """Testa connessione database"""
    try:
        db_manager = DatabaseManager()
        if db_manager.test_connection():
            tables = db_manager.get_tables()
            return jsonify({
                'status': 'Database connection OK',
                'tables': [t['table_name'] for t in tables],
                'total_tables': len(tables)
            }), 200
        else:
            return jsonify({'status': 'Database connection failed'}), 500
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/stats', methods=['GET'])
def get_stats():
    """Statistiche sistema"""
    if not analyzer:
        return jsonify({'error': 'Analyzer not initialized'}), 503

    return jsonify({
        'ai_available': bool(analyzer.ai_model and analyzer.ai_model.model),
        'mode': 'hybrid' if (analyzer.ai_model and analyzer.ai_model.model) else 'logic',
        'description': {
            'hybrid': 'AI per casi critici (urgenze, premium, best seller) + logica veloce per resto',
            'logic': 'Solo analisi logica veloce su metriche'
        }.get('hybrid' if (analyzer.ai_model and analyzer.ai_model.model) else 'logic'),
        'criteria_for_ai': [
            'Urgenze CRITICHE',
            'Prodotti premium (>€500)',
            'Best sellers con stock critico',
            'Slow movers con eccesso',
            'Revenue elevato (>€5000)'
        ] if (analyzer.ai_model and analyzer.ai_model.model) else []
    }), 200


@app.route('/api/ai-analytics', methods=['GET'])
def ai_analytics():
    """
    Endpoint analytics avanzato per dashboard AI.
    Restituisce KPI aggregati + breakdown urgenze + summary economico.
    """
    if not analyzer:
        return jsonify({
            'error': 'AI service not initialized'
        }), 503

    try:
        print("\n📊 Richiesta AI Analytics ricevuta...")

        results = analyzer.run_analysis()

        products = results.get("products", [])

        if not products:
            return jsonify({
                "status": "no_data",
                "message": "Nessun prodotto disponibile per analisi"
            }), 200

        # KPI aggregati
        total_products = len(products)
        total_revenue = sum(p['total_revenue'] for p in products)
        total_stock = sum(p['current_stock'] for p in products)
        total_investment = sum(
            p['suggested_order_quantity'] * p['price']
            for p in products
            if p['suggested_order_quantity'] > 0
        )

        urgency_breakdown = {
            "ALTA": len([p for p in products if p['urgency'] == 'ALTA']),
            "MEDIA": len([p for p in products if p['urgency'] == 'MEDIA']),
            "BASSA": len([p for p in products if p['urgency'] == 'BASSA']),
            "NESSUNA": len([p for p in products if p['urgency'] == 'NESSUNA'])
        }

        ai_used = len([p for p in products if p['analyzed_with_ai']])

        analytics_response = {
            "status": "success",
            "analysis_date": results.get("analysis_date"),
            "mode": results.get("ai_mode"),

            # KPI principali
            "kpi": {
                "total_products": total_products,
                "total_revenue": round(total_revenue, 2),
                "total_stock_units": total_stock,
                "total_investment_recommended": round(total_investment, 2),
                "ai_analyses": ai_used,
                "logic_analyses": total_products - ai_used
            },

            # Breakdown urgenze
            "urgency_distribution": urgency_breakdown,

            # Top 5 critici
            "top_critical_products": sorted(
                [p for p in products if p['urgency'] == 'ALTA'],
                key=lambda x: x['days_of_stock']
            )[:5],

            # Top revenue
            "top_revenue_products": sorted(
                products,
                key=lambda x: x['total_revenue'],
                reverse=True
            )[:5],

            # Nota sistema
            "system_note": results.get("note")
        }

        return jsonify(analytics_response), 200

    except Exception as e:
        print(f"❌ Errore AI Analytics: {e}")
        import traceback
        traceback.print_exc()
        return jsonify({
            "error": str(e),
            "type": type(e).__name__
        }), 500

if __name__ == '__main__':
    Config.print_config()
    print(f"🚀 Avvio Flask su porta {Config.FLASK_PORT}...\n")
    app.run(
        host=Config.FLASK_HOST,
        port=Config.FLASK_PORT,
        debug=False
    )
