def __init__(self):
    self.db_manager = DatabaseManager()
    self.ai_model = AIModelManager() if AI_AVAILABLE else None

    if self.ai_model and self.ai_model.model:
        print("✅ Analyzer con AI attivo!")
    else:
        print("✅ Analyzer base (senza AI)")
