import os
from pathlib import Path
from dotenv import load_dotenv

env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)

class Config:
    # Database
    DB_HOST = os.getenv('DB_HOST', 'localhost')
    DB_PORT = int(os.getenv('DB_PORT', 5432))
    DB_NAME = os.getenv('DB_NAME', 'myappdb')
    DB_USER = os.getenv('DB_USER', 'admin')
    DB_PASSWORD = os.getenv('DB_PASSWORD', 'admin123')

    # Model
    BASE_DIR = Path(__file__).parent.parent
    MODEL_PATH = os.getenv('MODEL_PATH', str(BASE_DIR / 'models' / 'mistral-7b-instruct-v0.2.Q4_K_M.gguf'))

    # AI Settings
    AI_MAX_TOKENS = int(os.getenv('AI_MAX_TOKENS', 2048))
    AI_TEMPERATURE = float(os.getenv('AI_TEMPERATURE', 0.7))
    AI_N_THREADS = int(os.getenv('AI_N_THREADS', 4))

    # Flask
    FLASK_PORT = int(os.getenv('FLASK_PORT', 5000))
    FLASK_HOST = os.getenv('FLASK_HOST', '0.0.0.0')

    @classmethod
    def get_db_config(cls):
        return {
            'host': cls.DB_HOST,
            'port': cls.DB_PORT,
            'database': cls.DB_NAME,
            'user': cls.DB_USER,
            'password': cls.DB_PASSWORD
        }

    @classmethod
    def print_config(cls):
        print("\n" + "="*60)
        print("⚙️  CONFIGURAZIONE")
        print("="*60)
        print(f"Database: {cls.DB_NAME}")
        print(f"Host: {cls.DB_HOST}:{cls.DB_PORT}")
        print(f"User: {cls.DB_USER}")
        print(f"Flask Port: {cls.FLASK_PORT}")
        print("="*60 + "\n")
