import os
import multiprocessing
from pathlib import Path
from dotenv import load_dotenv

env_path = Path(__file__).parent.parent / '.env'
load_dotenv(dotenv_path=env_path)


class Config:
    # ==================== DATABASE ====================
    DB_HOST = os.getenv('DB_HOST', 'localhost')
    DB_PORT = int(os.getenv('DB_PORT', 5432))
    DB_NAME = os.getenv('DB_NAME', 'myappdb')
    DB_USER = os.getenv('DB_USER', 'admin')
    DB_PASSWORD = os.getenv('DB_PASSWORD', 'admin123')
    
    # Connection Pooling (nuovo)
    DB_POOL_SIZE = int(os.getenv('DB_POOL_SIZE', 5))
    DB_MAX_OVERFLOW = int(os.getenv('DB_MAX_OVERFLOW', 10))
    DB_POOL_RECYCLE = int(os.getenv('DB_POOL_RECYCLE', 3600))
    DB_POOL_PRE_PING = os.getenv('DB_POOL_PRE_PING', 'true').lower() == 'true'
    
    # ==================== MODEL ====================
    BASE_DIR = Path(__file__).parent.parent
    MODEL_PATH = os.getenv('MODEL_PATH', str(BASE_DIR / 'models' / 'mistral-7b-instruct-v0.2.Q4_K_M.gguf'))
    
    # ==================== AI SETTINGS (OTTIMIZZATI) ====================
    
    # Auto-detect CPU cores
    _cpu_cores = multiprocessing.cpu_count()
    
    # Context size (riduci per velocità)
    AI_N_CTX = int(os.getenv('AI_N_CTX', 1024))  # 1024 invece di 2048 (2x più veloce)
    
    # Threads ottimizzati
    AI_N_THREADS = int(os.getenv('AI_N_THREADS', max(_cpu_cores // 2, 1)))  # Metà CPU cores
    AI_N_THREADS_BATCH = int(os.getenv('AI_N_THREADS_BATCH', _cpu_cores))  # Tutti i cores per batch
    
    # Batch size (critico per performance)
    AI_N_BATCH = int(os.getenv('AI_N_BATCH', 512))  # 512 ottimale per CPU
    
    # Generation settings
    AI_MAX_TOKENS = int(os.getenv('AI_MAX_TOKENS', 500))  # 250 invece di 2048 (prompt generation)
    AI_TEMPERATURE = float(os.getenv('AI_TEMPERATURE', 0.6))  # 0.6 più deterministico
    AI_TOP_P = float(os.getenv('AI_TOP_P', 0.9))
    AI_REPEAT_PENALTY = float(os.getenv('AI_REPEAT_PENALTY', 1.15))
    
    # Memory optimization
    AI_USE_MLOCK = os.getenv('AI_USE_MLOCK', 'true').lower() == 'true'  # Lock model in RAM
    AI_VERBOSE = os.getenv('AI_VERBOSE', 'false').lower() == 'true'  # Disabilita log verbosi
    
    # ==================== FLASK ====================
    FLASK_PORT = int(os.getenv('FLASK_PORT', 5001))
    FLASK_HOST = os.getenv('FLASK_HOST', '0.0.0.0')
    FLASK_DEBUG = os.getenv('FLASK_DEBUG', 'false').lower() == 'true'
    
    # Cache settings (nuovo)
    CACHE_TYPE = os.getenv('CACHE_TYPE', 'simple')
    CACHE_DEFAULT_TIMEOUT = int(os.getenv('CACHE_DEFAULT_TIMEOUT', 300))  # 5 minuti
    
    # ==================== BUSINESS LOGIC ====================
    
    # Query limits
    MAX_PRODUCTS_ANALYZE = int(os.getenv('MAX_PRODUCTS_ANALYZE', 100))  # Aumentato da 20
    
    # AI thresholds (quando usare AI vs logica)
    AI_THRESHOLD_PRICE = float(os.getenv('AI_THRESHOLD_PRICE', 500))  # Premium products
    AI_THRESHOLD_REVENUE = float(os.getenv('AI_THRESHOLD_REVENUE', 5000))  # High revenue
    AI_THRESHOLD_DAILY_RATE = float(os.getenv('AI_THRESHOLD_DAILY_RATE', 3))  # Best sellers
    
    # ==================== HELPERS ====================
    
    @classmethod
    def get_db_config(cls):
        """Configurazione database per psycopg2"""
        return {
            'host': cls.DB_HOST,
            'port': cls.DB_PORT,
            'database': cls.DB_NAME,
            'user': cls.DB_USER,
            'password': cls.DB_PASSWORD
        }
    
    @classmethod
    def get_database_url(cls):
        """SQLAlchemy connection string"""
        return f"postgresql://{cls.DB_USER}:{cls.DB_PASSWORD}@{cls.DB_HOST}:{cls.DB_PORT}/{cls.DB_NAME}"
    
    @classmethod
    def get_ai_config(cls):
        """Configurazione completa per AI model"""
        return {
            'model_path': cls.MODEL_PATH,
            'n_ctx': cls.AI_N_CTX,
            'n_threads': cls.AI_N_THREADS,
            'n_threads_batch': cls.AI_N_THREADS_BATCH,
            'n_batch': cls.AI_N_BATCH,
            'use_mlock': cls.AI_USE_MLOCK,
            'verbose': cls.AI_VERBOSE,
            'n_gpu_layers': 0  # CPU only
        }
    
    @classmethod
    def get_generation_config(cls):
        """Configurazione per AI text generation"""
        return {
            'max_tokens': cls.AI_MAX_TOKENS,
            'temperature': cls.AI_TEMPERATURE,
            'top_p': cls.AI_TOP_P,
            'repeat_penalty': cls.AI_REPEAT_PENALTY,
            'stop': ["</s>", "\n\n\n", "---"]
        }
    
    @classmethod
    def print_config(cls):
        """Stampa configurazione dettagliata"""
        print("\n" + "="*70)
        print("⚙️  CONFIGURAZIONE SISTEMA")
        print("="*70)
        
        # Database
        print("\n📊 DATABASE:")
        print(f"   Host: {cls.DB_HOST}:{cls.DB_PORT}")
        print(f"   Database: {cls.DB_NAME}")
        print(f"   User: {cls.DB_USER}")
        print(f"   Pool: {cls.DB_POOL_SIZE} connessioni (max overflow: {cls.DB_MAX_OVERFLOW})")
        
        # AI Model
        print("\n🤖 MODELLO AI:")
        model_name = Path(cls.MODEL_PATH).name if os.path.exists(cls.MODEL_PATH) else "NON TROVATO"
        print(f"   Modello: {model_name}")
        print(f"   Context size: {cls.AI_N_CTX} tokens")
        print(f"   Threads: {cls.AI_N_THREADS} (batch: {cls.AI_N_THREADS_BATCH})")
        print(f"   Batch size: {cls.AI_N_BATCH}")
        print(f"   Max output: {cls.AI_MAX_TOKENS} tokens")
        print(f"   Temperature: {cls.AI_TEMPERATURE}")
        print(f"   Memory lock: {'✅' if cls.AI_USE_MLOCK else '❌'}")
        
        # Performance
        print("\n⚡ PERFORMANCE:")
        print(f"   CPU cores: {cls._cpu_cores}")
        print(f"   Max prodotti analisi: {cls.MAX_PRODUCTS_ANALYZE}")
        print(f"   Cache timeout: {cls.CACHE_DEFAULT_TIMEOUT}s")
        
        # Flask
        print("\n🌐 FLASK API:")
        print(f"   Endpoint: http://{cls.FLASK_HOST}:{cls.FLASK_PORT}")
        print(f"   Debug mode: {'✅' if cls.FLASK_DEBUG else '❌'}")
        
        # AI Thresholds
        print("\n🎯 SOGLIE AI (quando usare AI approfondita):")
        print(f"   Prezzo premium: >€{cls.AI_THRESHOLD_PRICE}")
        print(f"   Revenue alto: >€{cls.AI_THRESHOLD_REVENUE}")
        print(f"   Daily rate: >{cls.AI_THRESHOLD_DAILY_RATE} unità/giorno")
        
        print("="*70 + "\n")
    
    @classmethod
    def validate(cls):
        """Valida configurazione"""
        errors = []
        
        # Check model file
        if not os.path.exists(cls.MODEL_PATH):
            errors.append(f"❌ Modello non trovato: {cls.MODEL_PATH}")
        
        # Check CPU threads
        if cls.AI_N_THREADS > cls._cpu_cores:
            errors.append(f"⚠️  AI_N_THREADS ({cls.AI_N_THREADS}) > CPU cores ({cls._cpu_cores})")
        
        # Check context size
        if cls.AI_N_CTX > 4096:
            errors.append(f"⚠️  AI_N_CTX molto alto ({cls.AI_N_CTX}) - rallenta inferenza")
        
        if errors:
            print("\n" + "="*70)
            print("⚠️  PROBLEMI DI CONFIGURAZIONE:")
            for error in errors:
                print(f"   {error}")
            print("="*70 + "\n")
        
        return len(errors) == 0
