import psycopg2
from psycopg2.extras import RealDictCursor
from config import Config


class DatabaseManager:
    def __init__(self):
        self.config = Config.get_db_config()

    def get_connection(self):
        """Crea connessione al database PostgreSQL (con RealDictCursor)"""
        return psycopg2.connect(
            **self.config,
            cursor_factory=RealDictCursor
        )

    def get_connection_pandas(self):
        """Connessione senza RealDictCursor - compatibile con pd.read_sql_query"""
        return psycopg2.connect(**self.config)

    def test_connection(self):
        """Testa la connessione al database"""
        try:
            conn = self.get_connection()
            with conn.cursor() as cur:
                cur.execute('SELECT version();')
                version = cur.fetchone()
                print(f'✅ Connessione DB OK: {version["version"][:50]}...')
            conn.close()
            return True
        except Exception as e:
            print(f'❌ Errore DB: {e}')
            return False

    def get_tables(self):
        """Ritorna lista delle tabelle nel database"""
        conn = self.get_connection()
        with conn.cursor() as cur:
            cur.execute("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = 'public'
                ORDER BY table_name;
            """)
            tables = cur.fetchall()
        conn.close()
        return tables


if __name__ == '__main__':
    db = DatabaseManager()
    db.test_connection()
