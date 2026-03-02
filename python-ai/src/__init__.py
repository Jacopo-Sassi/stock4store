import os
import sys

# Garantisce che il package veda i moduli locali
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from .ai_analyzer import PurchaseRecommendationAI, AIModelManager, AI_AVAILABLE
from database import DatabaseManager


class AnalyzerService:
    """
    Entry point dell'analyzer.
    - Crea DatabaseManager
    - Crea (una sola volta) il modello AI
    - Espone run_analysis() da usare nel resto del progetto
    """
    def __init__(self):
        # Gestione database
        self.db_manager = DatabaseManager()

        # Gestione modello AI (se disponibile)
        self.ai_model = AIModelManager() if AI_AVAILABLE else None

        if self.ai_model and self.ai_model.model:
            print("✅ Analyzer con AI attivo!")
        else:
            print("✅ Analyzer base (senza AI)")

        # Istanza principale che contiene tutta la logica ottimizzata
        self.recommender = PurchaseRecommendationAI()

    def run_analysis(self):
        """Esegue l’analisi completa e restituisce il JSON con i risultati."""
        return self.recommender.run_analysis()
