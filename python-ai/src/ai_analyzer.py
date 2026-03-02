import sys
import os
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

import pandas as pd
import numpy as np
from datetime import datetime
from scipy import stats          # pip install scipy
import json
from typing import Dict, Optional, Tuple

from database import DatabaseManager
from config import Config

# ── AI availability ───────────────────────────────────────────────────────────
try:
    from llama_cpp import Llama
    AI_AVAILABLE = True
    print("✅ llama-cpp-python disponibile!")
except ImportError:
    AI_AVAILABLE = False
    print("⚠️  llama-cpp-python non installato - Modalità base")


# ═══════════════════════════════════════════════════════════════════════════════
#  COSTANTI DI STAGIONALITÀ
# ═══════════════════════════════════════════════════════════════════════════════

# Coefficienti mensili per categoria (1.0 = media annuale).
# Valori basati su dinamiche tipiche della gioielleria italiana.
SEASONALITY: Dict[str, list] = {
    #               Gen   Feb   Mar   Apr   Mag   Giu   Lug   Ago   Set   Ott   Nov   Dic
    "ANELLI":    [0.70, 1.40, 0.90, 1.00, 1.30, 0.85, 0.80, 0.60, 0.90, 1.00, 0.95, 1.60],
    "COLLANE":   [0.65, 1.20, 0.95, 1.05, 1.40, 1.10, 0.90, 0.70, 0.95, 1.00, 1.00, 1.70],
    "BRACCIALI": [0.70, 1.30, 0.90, 1.00, 1.35, 1.05, 0.85, 0.65, 0.90, 1.00, 0.95, 1.65],
    "ORECCHINI": [0.70, 1.20, 0.95, 1.05, 1.30, 1.15, 0.90, 0.70, 0.95, 1.00, 0.95, 1.60],
    "FEDI":      [0.60, 1.50, 0.80, 0.90, 1.60, 1.10, 0.80, 0.50, 0.90, 0.95, 0.80, 1.55],
    "DEFAULT":   [0.70, 1.25, 0.90, 1.00, 1.30, 1.00, 0.85, 0.65, 0.90, 1.00, 0.95, 1.60],
}

# Giorni medi di lead time per ordine (personalizza in Config se possibile)
DEFAULT_LEAD_TIME_DAYS: int = getattr(Config, "LEAD_TIME_DAYS", 14)

# Soglia giorni senza vendite per classificare dead stock
DEAD_STOCK_DAYS: int = getattr(Config, "DEAD_STOCK_DAYS", 90)


# ═══════════════════════════════════════════════════════════════════════════════
#  AI MODEL MANAGER
# ═══════════════════════════════════════════════════════════════════════════════

class AIModelManager:
    """Gestisce il modello AI locale (Llama/Mistral GGUF)."""

    def __init__(self):
        self.model = None
        if not AI_AVAILABLE:
            return

        model_path = Config.MODEL_PATH
        if not os.path.exists(model_path):
            print(f"⚠️  Modello non trovato: {model_path}")
            print("   Scarica un modello GGUF e mettilo nella cartella models/")
            return

        print(f"🔄 Caricamento modello AI: {os.path.basename(model_path)}")
        try:
            ai_conf = Config.get_ai_config()
            self.model = Llama(**ai_conf)
            print("✅ Modello AI caricato!")
            print(
                f"   Context: {ai_conf['n_ctx']} | Threads: {ai_conf['n_threads']} "
                f"(batch: {ai_conf['n_threads_batch']}) | Batch: {ai_conf['n_batch']}"
            )
        except Exception as e:
            print(f"❌ Errore caricamento modello: {e}")

    def generate_ai_explanation(self, product_data: Dict) -> Optional[str]:
        """Genera analisi AI arricchita con trend, stagionalità e lead time."""
        if not self.model:
            return None

        trend_label = product_data.get("trend_label", "stabile")
        season_coeff = product_data.get("season_coefficient", 1.0)
        season_desc = (
            "alta stagionalità (domanda in crescita)" if season_coeff > 1.15
            else "bassa stagionalità (domanda in calo)" if season_coeff < 0.85
            else "stagionalità nella norma"
        )
        days_to_stockout = product_data["days_of_stock"]
        lead_time        = product_data.get("lead_time_days", DEFAULT_LEAD_TIME_DAYS)
        reorder_window   = max(0, days_to_stockout - lead_time)

        prompt = f"""Sei un esperto di inventory management per gioielleria. \
Analizza questo prodotto e fornisci una raccomandazione d'acquisto professionale in italiano.

Prodotto: {product_data['product_name']}
Categoria: {product_data['category']}
Stock attuale: {product_data['current_stock']} unità
Vendite totali: {product_data['total_sold']} unità
Velocità vendita: {product_data['daily_sales_rate']} unità/giorno
Giorni di stock: {days_to_stockout} giorni
Lead time fornitore: {lead_time} giorni
Finestra di riordino: {reorder_window} giorni rimasti prima dell'urgenza
Revenue totale: €{product_data['total_revenue']:.2f}
Prezzo unitario: €{product_data['price']:.2f}
Trend: {trend_label} ({product_data.get('trend_slope', 0):+.3f} unità/giorno²)
Stagionalità mese corrente: {season_desc} (coeff. {season_coeff:.2f})
Affidabilità dati: {product_data['data_reliability']} (±{product_data['rate_confidence_pct']}%)
Score priorità: {product_data.get('priority_score', 0):.1f}/100

Fornisci:
1. Valutazione situazione attuale (includi trend e stagionalità)
2. Urgenza motivata con finestra temporale concreta
3. Quantità da ordinare considerando lead time e picco stagionale
4. Insight strategici (pricing, posizionamento, rischio dead stock)

Sii conciso e professionale (max 180 parole)."""

        try:
            gen_conf = Config.get_generation_config()
            response = self.model(
                prompt,
                max_tokens=gen_conf["max_tokens"],
                temperature=gen_conf["temperature"],
                top_p=gen_conf["top_p"],
                repeat_penalty=gen_conf["repeat_penalty"],
                stop=gen_conf["stop"],
            )
            return response["choices"][0]["text"].strip()
        except Exception as e:
            print(f"⚠️  Errore generazione AI: {e}")
            return None


# ═══════════════════════════════════════════════════════════════════════════════
#  HELPER: STAGIONALITÀ & TREND
# ═══════════════════════════════════════════════════════════════════════════════

def get_season_coefficient(category: str, month: Optional[int] = None) -> float:
    """Restituisce il coefficiente stagionale per categoria e mese (default: mese corrente)."""
    if month is None:
        month = datetime.now().month
    key = str(category).upper()
    coeffs = SEASONALITY.get(key, SEASONALITY["DEFAULT"])
    return coeffs[month - 1]


def get_next_peak_coefficient(category: str) -> Tuple[float, int]:
    """
    Calcola il coefficiente massimo nei prossimi 3 mesi (incluso il corrente).
    Ritorna (max_coeff, mesi_al_picco).
    """
    current_month = datetime.now().month
    key = str(category).upper()
    coeffs = SEASONALITY.get(key, SEASONALITY["DEFAULT"])
    window = [(coeffs[(current_month - 1 + i) % 12], i) for i in range(3)]
    max_coeff, months_ahead = max(window, key=lambda x: x[0])
    return max_coeff, months_ahead


def compute_trend(
    product_id: str,
    conn,
    lookback_days: int = 90
) -> Tuple[float, str, float]:
    """
    Calcola il trend di vendita recente tramite regressione lineare su finestre settimanali.

    Returns:
        slope      – variazione media di vendite/settimana
        label      – "crescente" | "stabile" | "calante"
        r_squared  – bontà del fit (0–1)
    """
    query = f"""
        SELECT
            DATE_TRUNC('week', o.data_ordine) AS week,
            COALESCE(SUM(oi.quantita), 0)      AS qty
        FROM ordini_items oi
        JOIN ordini o ON oi.ordine_id = o.id
        WHERE oi.codice_articolo = %s
          AND o.data_ordine >= CURRENT_DATE - INTERVAL '{lookback_days} days'
        GROUP BY 1
        ORDER BY 1
    """
    try:
        df = pd.read_sql_query(query, conn, params=(product_id,))
        if len(df) < 3:
            return 0.0, "stabile", 0.0

        x = np.arange(len(df))
        y = df["qty"].values.astype(float)
        slope, _, r_value, _, _ = stats.linregress(x, y)
        r_squared = r_value ** 2

        if slope > 0.15 and r_squared > 0.25:
            label = "crescente"
        elif slope < -0.15 and r_squared > 0.25:
            label = "calante"
        else:
            label = "stabile"

        return round(slope, 4), label, round(r_squared, 3)
    except Exception:
        return 0.0, "stabile", 0.0


# ═══════════════════════════════════════════════════════════════════════════════
#  PURCHASE RECOMMENDATION ENGINE
# ═══════════════════════════════════════════════════════════════════════════════

class PurchaseRecommendationAI:

    # Prior Bayesiani per tasso di vendita giornaliero per categoria
    CATEGORY_PRIORS = {
        "ANELLI":    0.3,
        "COLLANE":   0.5,
        "BRACCIALI": 0.4,
        "ORECCHINI": 0.4,
        "FEDI":      0.2,
        "DEFAULT":   0.3,
    }
    PRIOR_STRENGTH = 5  # Equivalente a N ordini "virtuali" di prior

    def __init__(self):
        self.db_manager  = DatabaseManager()
        self.ai_model    = AIModelManager() if AI_AVAILABLE else None
        self._trend_conn = None  # connessione riusabile per trend queries

        mode = "🤖 AI ibrido" if (self.ai_model and self.ai_model.model) else "📊 Logica veloce"
        print(f"{mode} attivo | Lead time: {DEFAULT_LEAD_TIME_DAYS}gg | Dead stock: {DEAD_STOCK_DAYS}gg")

    # ── DATA EXTRACTION ───────────────────────────────────────────────────────

    def extract_sales_data(self) -> pd.DataFrame:
        """Estrae dati vendite + metriche stagionali base dal database."""
        conn = None
        try:
            conn = self.db_manager.get_connection_pandas()
            self._trend_conn = conn   # riuso per trend queries

            query = f"""
                WITH sales_summary AS (
                    SELECT
                        s.codarticolo                                        AS codice_articolo,
                        COUNT(DISTINCT s.contatore)                          AS total_orders,
                        COALESCE(SUM(CAST(s.quantita AS numeric)), 0)        AS total_quantity_sold,
                        COALESCE(AVG(CAST(s.quantita AS numeric)), 0)        AS avg_quantity_per_order,
                        COALESCE(SUM(CAST(REPLACE(s.importo, ',', '.') AS numeric)), 0)   AS total_revenue,
                        MIN(s.dataora)                                       AS first_sale_date,
                        MAX(s.dataora)                                       AS last_sale_date,
                        -- Vendite ultimi 30 giorni
                        COALESCE(SUM(
                            CASE WHEN s.dataora >= CURRENT_DATE - INTERVAL '30 days'
                                 THEN CAST(s.quantita AS numeric) ELSE 0 END
                        ), 0)                                                AS qty_last_30d,
                        -- Vendite stessa finestra anno scorso (YoY)
                        COALESCE(SUM(
                            CASE WHEN s.dataora BETWEEN
                                      CURRENT_DATE - INTERVAL '395 days'
                                  AND CURRENT_DATE - INTERVAL '335 days'
                                 THEN CAST(s.quantita AS numeric) ELSE 0 END
                        ), 0)                                                AS qty_same_period_ly
                    FROM sco_dettaglio_sto s
                    WHERE s.dataora >= CURRENT_DATE - INTERVAL '365 days'
                      AND s.annullato = ' '       -- escludi righe annullate
                      AND s.tipo_mov = '-'        -- solo vendite, non resi
                      AND s.codarticolo IS NOT NULL
                    GROUP BY s.codarticolo
                    HAVING SUM(CAST(s.quantita AS numeric)) > 0
                )
                SELECT
                    a.codice                                   AS product_id,
                    a.descrizione                              AS product_name,
                    COALESCE(a.gruppo, 'N/A')                  AS category,
                    COALESCE(a.prezzodilistino, 0)             AS price,
                    COALESCE(ast.quantita_stock, 0)            AS current_stock,
                    ss.total_orders,
                    ss.total_quantity_sold,
                    ss.avg_quantity_per_order,
                    ss.total_revenue,
                    ss.first_sale_date,
                    ss.last_sale_date,
                    ss.qty_last_30d,
                    ss.qty_same_period_ly
                FROM articoli a
                LEFT JOIN articolo_stock ast ON a.codice = ast.codice_articolo
                JOIN sales_summary ss        ON a.codice = ss.codice_articolo
                ORDER BY ss.total_revenue DESC
                LIMIT 100;
            """

            df = pd.read_sql_query(query, conn)

            if not df.empty and df["product_id"].iloc[0] == "product_id":
                raise ValueError("DataFrame contiene header come valori - connessione anomala")

            if df.empty:
                print("⚠️  Nessun dato trovato!")
            else:
                print(f"📊 Estratti {len(df)} prodotti dal database")

            return df

        except Exception as e:
            print(f"❌ Errore estrazione dati: {e}")
            import traceback; traceback.print_exc()
            raise
        finally:
            # Non chiudiamo qui: la connessione serve per i trend
            pass

    # ── LOGIC EXPLANATION ─────────────────────────────────────────────────────

    def _generate_logic_explanation_fast(self, row) -> str:
        stock       = int(row["current_stock"])
        sold        = int(row["total_quantity_sold"])
        rate        = float(row["daily_sales_rate"])
        rate_adj    = float(row.get("adjusted_sales_rate", rate))
        days        = float(row["days_of_stock"])
        revenue     = float(row["total_revenue"])
        urgency     = row["urgency"]
        suggested   = int(row["suggested_order_quantity"])
        price       = float(row["price"])
        reliability = row.get("data_reliability", "N/D")
        trend_lbl   = row.get("trend_label", "stabile")
        season_c    = float(row.get("season_coefficient", 1.0))
        yoy         = float(row.get("yoy_growth", 0.0))
        lead_time   = int(row.get("lead_time_days", DEFAULT_LEAD_TIME_DAYS))
        is_dead     = bool(row.get("is_dead_stock", False))
        priority    = float(row.get("priority_score", 0))

        lines = [
            f"📊 {row['product_name']}",
            (
                f"Stock: {stock}u | Vendite: {sold} | Rate base: {rate}/g | "
                f"Rate aggiustato: {rate_adj}/g | Durata: {days:.1f}gg | "
                f"Revenue: €{revenue:.2f} | Affidabilità: {reliability}"
            ),
            (
                f"📈 Trend: {trend_lbl} | Stagionalità: {season_c:.2f}x | "
                f"YoY: {yoy:+.1f}% | Lead time: {lead_time}gg | "
                f"Priority score: {priority:.1f}/100"
            ),
        ]

        if is_dead:
            lines.append("\n💀 DEAD STOCK: Nessuna vendita recente. Valuta promozione o liquidazione.")
        elif urgency == "ALTA":
            reorder_window = max(0, days - lead_time)
            lines.append(
                f"\n🔴 URGENZA CRITICA: Stock per {days:.1f}gg (lead time {lead_time}gg). "
                f"Finestra riordino: {reorder_window:.0f}gg. Ordina subito {suggested}u."
            )
            if rate_adj > 2:
                lines.append(f"   ⚡ Alta rotazione stagionale ({rate_adj}/g) - Priorità massima!")
        elif urgency == "MEDIA":
            lines.append(
                f"\n🟡 ATTENZIONE: Stock per {days:.1f}gg. "
                f"Ordina {suggested}u entro {max(1, int(days - lead_time))} giorni."
            )
        elif urgency == "BASSA":
            lines.append(f"\n🟢 OK: Stock sufficiente ({days:.1f}gg). Monitora il trend.")
        else:
            lines.append(f"\n⚪ ECCESSO: Stock {days:.1f}gg. Non ordinare.")

        if suggested > 0:
            invest = suggested * price
            lines.append(f"\n💰 Investimento: €{invest:.2f} → Revenue atteso: €{invest * 1.3:.2f}")

        badges = []
        if price > 100:           badges.append("💎 Premium")
        if rate_adj > 3:          badges.append("⚡ Best Seller")
        if rate_adj < 0.5 and stock > 20: badges.append("⚠️ Slow Mover")
        if trend_lbl == "crescente":  badges.append("📈 In crescita")
        if trend_lbl == "calante":    badges.append("📉 In calo")
        if season_c > 1.2:        badges.append("🌟 Alta stagione")
        if yoy > 20:              badges.append("🚀 YoY +")
        if yoy < -20:             badges.append("⬇️ YoY -")
        if badges:
            lines.append(f"\n{' | '.join(badges)}")

        lines.append(f"\n{'─'*60}\n⚡ Analisi logica veloce")
        return "\n".join(lines)

    # ── METRICS ENGINE ────────────────────────────────────────────────────────

    def calculate_metrics(self, sales_df: pd.DataFrame) -> Dict:
        if sales_df.empty:
            return self._empty_result()

        df = sales_df.copy()

        # Date
        df["first_sale_date"] = pd.to_datetime(df["first_sale_date"], errors="coerce")
        df["last_sale_date"]  = pd.to_datetime(df["last_sale_date"],  errors="coerce")

        # Tipi numerici
        for col in ["current_stock", "total_quantity_sold", "price",
                    "total_revenue", "total_orders", "qty_last_30d", "qty_same_period_ly"]:
            df[col] = pd.to_numeric(df[col], errors="coerce").fillna(0)

        df["days_active"] = (
            (df["last_sale_date"] - df["first_sale_date"]).dt.days
        ).clip(lower=1)

        # ── 1. TASSO DI VENDITA BAYESIANO ────────────────────────────────────
        df["_raw_rate"] = (df["total_quantity_sold"] / df["days_active"]).fillna(0)

        def bayesian_rate(row):
            n        = row["total_orders"]
            observed = row["_raw_rate"]
            prior    = self.CATEGORY_PRIORS.get(
                str(row["category"]).upper(), self.CATEGORY_PRIORS["DEFAULT"]
            )
            w = n / (n + self.PRIOR_STRENGTH)
            return round(w * observed + (1 - w) * prior, 4)

        df["daily_sales_rate"] = df.apply(bayesian_rate, axis=1).round(4)
        df.drop(columns=["_raw_rate"], inplace=True)

        # ── 2. STAGIONALITÀ ──────────────────────────────────────────────────
        current_month = datetime.now().month
        df["season_coefficient"] = df["category"].apply(
            lambda c: get_season_coefficient(c, current_month)
        )
        df["peak_coeff"], df["months_to_peak"] = zip(
            *df["category"].apply(get_next_peak_coefficient)
        )

        # Tasso aggiustato per stagionalità corrente
        df["adjusted_sales_rate"] = (
            df["daily_sales_rate"] * df["season_coefficient"]
        ).round(4)

        # ── 3. TREND (regressione lineare) ───────────────────────────────────
        trend_data = []
        conn = self._trend_conn
        if conn:
            for pid in df["product_id"]:
                slope, label, r2 = compute_trend(pid, conn, lookback_days=90)
                trend_data.append({"product_id": pid, "trend_slope": slope,
                                   "trend_label": label, "trend_r2": r2})
        else:
            trend_data = [{"product_id": pid, "trend_slope": 0.0,
                           "trend_label": "stabile", "trend_r2": 0.0}
                          for pid in df["product_id"]]

        trend_df = pd.DataFrame(trend_data)
        df = df.merge(trend_df, on="product_id", how="left")

        # ── 4. YoY GROWTH ────────────────────────────────────────────────────
        df["yoy_growth"] = np.where(
            df["qty_same_period_ly"] > 0,
            ((df["qty_last_30d"] - df["qty_same_period_ly"]) / df["qty_same_period_ly"] * 100).round(1),
            0.0
        )

        # ── 5. DEAD STOCK ────────────────────────────────────────────────────
        days_since_last_sale = (
            pd.Timestamp.now() - df["last_sale_date"]
        ).dt.days.fillna(999)
        df["days_since_last_sale"] = days_since_last_sale
        df["is_dead_stock"] = (
            (days_since_last_sale > DEAD_STOCK_DAYS) & (df["current_stock"] > 0)
        )

        # ── 6. AFFIDABILITÀ STATISTICA ───────────────────────────────────────
        def reliability_label(n):
            n = int(n)
            if n <= 2:    return "⚠️ SCARSA"
            elif n <= 5:  return "🟡 BASSA"
            elif n <= 20: return "🟢 MEDIA"
            else:         return "✅ ALTA"

        df["data_reliability"]    = df["total_orders"].apply(reliability_label)
        df["rate_confidence_pct"] = df["total_orders"].apply(
            lambda n: max(10, round(100 - (int(n) / (int(n) + self.PRIOR_STRENGTH)) * 90))
        )

        # ── 7. GIORNI DI STOCK (con tasso aggiustato) ────────────────────────
        df["days_of_stock"] = (
            df["current_stock"] / df["adjusted_sales_rate"].replace(0, np.nan)
        ).replace([np.inf, -np.inf], 999).fillna(999).round(1)

        df["lead_time_days"] = DEFAULT_LEAD_TIME_DAYS

        # ── 8. URGENZA ───────────────────────────────────────────────────────
        # Soglie adattate al lead time: se stockout entro lead_time → ALTA
        high_thresh   = max(7,  DEFAULT_LEAD_TIME_DAYS)
        medium_thresh = max(30, DEFAULT_LEAD_TIME_DAYS * 2)

        conditions = [
            df["days_of_stock"] < high_thresh,
            df["days_of_stock"] < medium_thresh,
            df["days_of_stock"] < 90,
        ]
        df["urgency"]      = np.select(conditions, ["ALTA", "MEDIA", "BASSA"], default="NESSUNA")
        df["stock_status"] = np.select(conditions, ["CRITICO", "BASSO", "NORMALE"], default="ECCESSO")

        # Override dead stock
        df.loc[df["is_dead_stock"], "urgency"]      = "DEAD_STOCK"
        df.loc[df["is_dead_stock"], "stock_status"] = "DEAD_STOCK"

        # ── 9. QUANTITÀ SUGGERITE ─────────────────────────────────────────────
        # Copre lead_time + buffer stagionale basato sul picco nei prossimi 3 mesi
        df["suggested_order_quantity"] = 0

        alta_mask  = df["urgency"] == "ALTA"
        media_mask = df["urgency"] == "MEDIA"

        # ALTA: copri (lead_time + 30gg) con il tasso aggiustato al picco futuro
        df.loc[alta_mask, "suggested_order_quantity"] = (
            df.loc[alta_mask, "adjusted_sales_rate"] *
            df.loc[alta_mask, "peak_coeff"] *
            (DEFAULT_LEAD_TIME_DAYS + 30)
        ).astype(int)

        # MEDIA: copri 20gg con tasso aggiustato
        df.loc[media_mask, "suggested_order_quantity"] = (
            df.loc[media_mask, "adjusted_sales_rate"] * 20
        ).astype(int)

        # Dead stock → non ordinare
        df.loc[df["is_dead_stock"], "suggested_order_quantity"] = 0

        # ── 10. PRIORITY SCORE (0–100) ────────────────────────────────────────
        # Componenti: urgenza (40), revenue normalizzata (25), trend (20), stagionalità (15)
        max_revenue = df["total_revenue"].max() or 1

        urgency_score_map = {"ALTA": 40, "MEDIA": 25, "BASSA": 10, "NESSUNA": 0, "DEAD_STOCK": 0}
        trend_score_map   = {"crescente": 20, "stabile": 10, "calante": 0}

        df["priority_score"] = (
            df["urgency"].map(urgency_score_map).fillna(0) +
            (df["total_revenue"] / max_revenue * 25).round(1) +
            df["trend_label"].map(trend_score_map).fillna(10) +
            ((df["season_coefficient"] - 0.6) / (1.7 - 0.6) * 15).clip(0, 15).round(1)
        ).round(1)

        # ── 11. SPIEGAZIONI ───────────────────────────────────────────────────
        df["explanation"] = df.apply(self._generate_logic_explanation_fast, axis=1)

        # ── 12. AI SELETTIVA ──────────────────────────────────────────────────
        ai_count = 0
        df["needs_ai"] = False

        if self.ai_model and self.ai_model.model:
            ai_mask = (
                (df["urgency"] == "ALTA") |
                (df["price"] > Config.AI_THRESHOLD_PRICE) |
                (df["priority_score"] > 70) |
                ((df["adjusted_sales_rate"] > Config.AI_THRESHOLD_DAILY_RATE) & (df["days_of_stock"] < 10)) |
                (df["total_revenue"] > Config.AI_THRESHOLD_REVENUE) |
                (df["trend_label"] == "crescente") & (df["urgency"].isin(["ALTA", "MEDIA"]))
            )
            df["needs_ai"] = ai_mask
            ai_count = int(df["needs_ai"].sum())
            print(f"   🤖 Generazione AI per {ai_count} prodotti critici...")

            for idx, row in df[df["needs_ai"]].iterrows():
                product_data = {
                    "product_name":             row["product_name"],
                    "category":                 row["category"],
                    "current_stock":            int(row["current_stock"]),
                    "total_sold":               int(row["total_quantity_sold"]),
                    "daily_sales_rate":         float(row["daily_sales_rate"]),
                    "adjusted_sales_rate":      float(row["adjusted_sales_rate"]),
                    "days_of_stock":            float(row["days_of_stock"]),
                    "total_revenue":            float(row["total_revenue"]),
                    "urgency":                  row["urgency"],
                    "suggested_order_quantity": int(row["suggested_order_quantity"]),
                    "price":                    float(row["price"]),
                    "data_reliability":         row["data_reliability"],
                    "rate_confidence_pct":      int(row["rate_confidence_pct"]),
                    "trend_label":              row.get("trend_label", "stabile"),
                    "trend_slope":              float(row.get("trend_slope", 0)),
                    "season_coefficient":       float(row.get("season_coefficient", 1.0)),
                    "yoy_growth":               float(row.get("yoy_growth", 0)),
                    "lead_time_days":           int(row.get("lead_time_days", DEFAULT_LEAD_TIME_DAYS)),
                    "priority_score":           float(row.get("priority_score", 0)),
                }
                ai_text = self.ai_model.generate_ai_explanation(product_data)
                if ai_text:
                    df.at[idx, "explanation"] = (
                        f"🤖 ANALISI AI ({row['urgency']} | Score {row['priority_score']:.0f}):\n\n"
                        f"{ai_text}\n\n{'─'*60}\n✨ Generato da AI"
                    )

        # ── CLEANUP & OUTPUT ──────────────────────────────────────────────────
        df["analyzed_with_ai"] = df["needs_ai"]
        df["recommendation"] = (
            "Ordinare " + df["suggested_order_quantity"].astype(str) + "u"
            " | Urgenza: " + df["urgency"] +
            " | Score: " + df["priority_score"].astype(str) +
            " | Affidabilità: " + df["data_reliability"]
        )

        drop_cols = ["days_active", "needs_ai", "first_sale_date", "last_sale_date",
                     "_raw_rate", "peak_coeff"]
        df.drop(columns=[c for c in drop_cols if c in df.columns], inplace=True)

        total          = len(df)
        logic_analyses = total - ai_count

        return {
            "products":                 df.to_dict("records"),
            "analysis_date":            datetime.now().isoformat(),
            "total_products_analyzed":  total,
            "ai_enabled":               bool(self.ai_model and self.ai_model.model),
            "ai_analyses_performed":    ai_count,
            "logic_analyses_performed": logic_analyses,
            "ai_mode":                  "hybrid" if (self.ai_model and self.ai_model.model) else "logic",
            "efficiency_percent":       round(logic_analyses / total * 100, 1) if total > 0 else 0,
            "season_month":             datetime.now().strftime("%B %Y"),
            "lead_time_days":           DEFAULT_LEAD_TIME_DAYS,
            "note": (
                f"Analisi ibrida: {ai_count} AI + {logic_analyses} logica veloce"
                if (self.ai_model and self.ai_model.model) else
                "Analisi logica veloce per tutti i prodotti"
            ),
        }

    # ── UTILS ─────────────────────────────────────────────────────────────────

    def _empty_result(self) -> Dict:
        return {
            "products": [], "analysis_date": datetime.now().isoformat(),
            "total_products_analyzed": 0, "ai_enabled": False,
            "ai_analyses_performed": 0, "logic_analyses_performed": 0,
            "ai_mode": "logic", "efficiency_percent": 0,
            "note": "Nessun dato disponibile",
        }

    def run_analysis(self) -> Dict:
        print("\n" + "=" * 70)
        print("🚀 AVVIO ANALISI PURCHASE RECOMMENDATION")
        print("=" * 70 + "\n")

        print("📥 Estrazione dati vendite...")
        sales_df = self.extract_sales_data()

        print("🔢 Calcolo metriche...")
        metrics = self.calculate_metrics(sales_df)

        # Chiudi connessione ora che trend sono calcolati
        if self._trend_conn:
            try: self._trend_conn.close()
            except Exception: pass

        print(f"✅ Analizzati {metrics['total_products_analyzed']} prodotti")
        if metrics["ai_enabled"]:
            print(f"   🤖 AI: {metrics['ai_analyses_performed']} | "
                  f"⚡ Logic: {metrics['logic_analyses_performed']} | "
                  f"📊 Efficienza: {metrics['efficiency_percent']}%\n")
        return metrics


# ═══════════════════════════════════════════════════════════════════════════════
#  MAIN
# ═══════════════════════════════════════════════════════════════════════════════

def main():
    try:
        analyzer = PurchaseRecommendationAI()
        results  = analyzer.run_analysis()

        output_dir  = os.path.join(os.path.dirname(__file__), "..", "output")
        os.makedirs(output_dir, exist_ok=True)
        output_file = os.path.join(
            output_dir,
            f"recommendations_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        )
        with open(output_file, "w", encoding="utf-8") as f:
            json.dump(results, f, indent=2, ensure_ascii=False, default=str)

        print(f"💾 Risultati salvati: {output_file}\n")

    except Exception as e:
        print(f"\n❌ ERRORE: {e}")
        import traceback; traceback.print_exc()


if __name__ == "__main__":
    main()
