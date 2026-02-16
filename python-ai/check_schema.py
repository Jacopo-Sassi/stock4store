import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), 'src'))

from database import DatabaseManager

def main():
    print("\n" + "="*70)
    print("🔍 ANALISI SCHEMA DATABASE")
    print("="*70 + "\n")

    db = DatabaseManager()
    conn = db.get_connection()

    with conn.cursor() as cur:
        cur.execute("""
                    SELECT table_name
                    FROM information_schema.tables
                    WHERE table_schema = 'public'
                    ORDER BY table_name;
                    """)
        tables = cur.fetchall()

        print(f"📊 Trovate {len(tables)} tabelle:\n")

        for table in tables:
            table_name = table['table_name']

            # Colonne
            cur.execute("""
                        SELECT
                            column_name,
                            data_type,
                            is_nullable,
                            column_default
                        FROM information_schema.columns
                        WHERE table_schema = 'public'
                          AND table_name = %s
                        ORDER BY ordinal_position;
                        """, (table_name,))

            columns = cur.fetchall()

            # Count record
            cur.execute(f"SELECT COUNT(*) as count FROM {table_name};")
            count = cur.fetchone()

            print(f"📋 {table_name.upper()} ({count['count']} record)")
            print("-" * 70)

            for col in columns:
                nullable = "NULL" if col['is_nullable'] == 'YES' else "NOT NULL"
                default = f" DEFAULT {col['column_default']}" if col['column_default'] else ""
                print(f"   {col['column_name']:<30} {col['data_type']:<20} {nullable}{default}")

            print()

    conn.close()
    print("="*70 + "\n")

if __name__ == "__main__":
    main()
