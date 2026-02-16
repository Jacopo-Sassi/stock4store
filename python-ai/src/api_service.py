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

    return jsonify({
        'status': 'healthy' if analyzer and db_healthy else 'unhealthy',
        'service': 'AI Recommendation Engine',
        'analyzer_loaded': analyzer is not None,
        'database_connected': db_healthy,
        'ai_mode': 'basic'
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
                'tables': [t['table_name'] for t in tables]
            }), 200
        else:
            return jsonify({'status': 'Database connection failed'}), 500
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    Config.print_config()
    print(f"🚀 Avvio Flask su porta {Config.FLASK_PORT}...\n")
    app.run(
        host=Config.FLASK_HOST,
        port=Config.FLASK_PORT,
        debug=False
    )
