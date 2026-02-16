import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), 'src'))

from database import DatabaseManager
from config import Config

def main():
    print("\n" + "="*70)
    print("🔍 TEST CONNESSIONE DATABASE POSTGRESQL")
    print("="*70)
    print(f"📊 Database: {Config.DB_NAME}")
    print(f"🌐 Host: {Config.DB_HOST}:{Config.DB_PORT}")
    print(f"👤 User: {Config.DB_USER}")
    print("="*70 + "\n")

    db = DatabaseManager()

    # Test connessione
    print("🔄 Tentativo connessione...")
    if not db.test_connection():
        print("\n❌ ERRORE: Impossibile connettersi al database!")
        print("   Verifica che PostgreSQL sia avviato sulla porta 5432")
        return False

    # Lista tabelle
    print("\n📋 Analisi tabelle nel database:\n")
    try:
        conn = db.get_connection()
        with conn.cursor() as cur:
            # Conta tabelle
            cur.execute("""
                        SELECT table_name
                        FROM information_schema.tables
                        WHERE table_schema = 'public'
                        ORDER BY table_name;
                        """)
            tables = cur.fetchall()

            if not tables:
                print("   ⚠️  Nessuna tabella trovata nel database!")
                print("   Assicurati che Liquibase abbia creato le tabelle.")
                conn.close()
                return False

            print(f"   Trovate {len(tables)} tabelle:\n")
            total_records = 0

            for table in tables:
                table_name = table['table_name']
                try:
                    cur.execute(f"SELECT COUNT(*) as count FROM {table_name};")
                    count = cur.fetchone()
                    record_count = count['count']
                    total_records += record_count

                    emoji = "✅" if record_count > 0 else "📋"
                    print(f"   {emoji} {table_name:<35} {record_count:>6} record")
                except Exception as e:
                    print(f"   ❌ {table_name:<35} (errore)")

            print(f"\n   📊 Totale record nel database: {total_records}")

        conn.close()

        print("\n" + "="*70)
        print("✅ DATABASE PRONTO PER L'ANALISI!")
        print("="*70 + "\n")
        return True

    except Exception as e:
        print(f"\n❌ ERRORE durante l'analisi: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    main()
