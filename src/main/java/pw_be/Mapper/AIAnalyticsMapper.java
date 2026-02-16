package pw_be.Mapper;

import org.example.pw_be.model.dto.AIAnalyticsResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class AIAnalyticsMapper {

    public AIAnalyticsResponseDto mapToDto(Map<String, Object> pythonResponse) {
        AIAnalyticsResponseDto dto = new AIAnalyticsResponseDto();

        try {
            // Estrai dati dalla risposta Python
            List<Map<String, Object>> products = (List<Map<String, Object>>) pythonResponse.get("products");
            Integer totalProducts = (Integer) pythonResponse.get("total_products_analyzed");
            String analysisDate = (String) pythonResponse.get("analysis_date");
            Boolean aiEnabled = (Boolean) pythonResponse.get("ai_enabled");
            String note = (String) pythonResponse.get("note");
            Integer aiCount = (Integer) pythonResponse.getOrDefault("ai_analyses_performed", 0);
            Integer logicCount = (Integer) pythonResponse.getOrDefault("logic_analyses_performed", 0);

            // ========== INSIGHTS ==========
            StringBuilder insights = new StringBuilder();
            insights.append("📊 ANALISI COMPLETATA\n\n");
            insights.append("Prodotti analizzati: ").append(totalProducts != null ? totalProducts : 0).append("\n");
            insights.append("Modalità: ").append(aiEnabled ? "AI Ibrida" : "Logica Veloce").append("\n");

            if (aiCount != null && aiCount > 0) {
                insights.append("• Analisi AI approfondite: ").append(aiCount).append("\n");
                insights.append("• Analisi logica veloce: ").append(logicCount).append("\n");
            }

            insights.append("\nData: ").append(analysisDate != null ? analysisDate : "N/A").append("\n");

            if (note != null) {
                insights.append("\n").append(note);
            }

            // Conta urgenze
            if (products != null && !products.isEmpty()) {
                long critici = products.stream()
                        .filter(p -> "ALTA".equals(p.get("urgency")))
                        .count();
                long medi = products.stream()
                        .filter(p -> "MEDIA".equals(p.get("urgency")))
                        .count();

                insights.append("\n\n🚨 URGENZE:\n");
                if (critici > 0) {
                    insights.append("• ").append(critici).append(" prodotti CRITICI\n");
                }
                if (medi > 0) {
                    insights.append("• ").append(medi).append(" prodotti con attenzione MEDIA\n");
                }
            }

            dto.setInsights(insights.toString());

            // ========== RACCOMANDAZIONI (UNA COMPLETA PER PRODOTTO) ==========
            List<String> raccomandazioniList = new ArrayList<>();

            if (products != null && !products.isEmpty()) {
                int counter = 1;

                // Ordina per urgenza
                products.stream()
                        .sorted((p1, p2) -> {
                            String u1 = (String) p1.get("urgency");
                            String u2 = (String) p2.get("urgency");
                            if ("ALTA".equals(u1)) return -1;
                            if ("ALTA".equals(u2)) return 1;
                            if ("MEDIA".equals(u1)) return -1;
                            if ("MEDIA".equals(u2)) return 1;
                            return 0;
                        })
                        .forEach(product -> {
                            StringBuilder analisiProdotto = new StringBuilder();

                            // ========== HEADER ==========
                            analisiProdotto.append("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
                            analisiProdotto.append("║  PRODOTTO #").append(counter).append("\n");
                            analisiProdotto.append("╚═══════════════════════════════════════════════════════════════════════════════╝\n\n");

                            // Icona urgenza
                            String urgency = (String) product.get("urgency");
                            String icon = "ALTA".equals(urgency) ? "🔴" :
                                    "MEDIA".equals(urgency) ? "🟡" :
                                            "BASSA".equals(urgency) ? "🟢" : "⚪";

                            // Nome e categoria
                            analisiProdotto.append(icon).append(" ")
                                    .append(product.get("product_name"))
                                    .append("\n");
                            analisiProdotto.append("Categoria: ")
                                    .append(product.get("category"))
                                    .append(" | Codice: ")
                                    .append(product.get("product_id"))
                                    .append("\n\n");

                            // ========== DATI STOCK E VENDITE ==========
                            analisiProdotto.append("📦 STOCK E VENDITE:\n");
                            analisiProdotto.append("─".repeat(80)).append("\n");
                            analisiProdotto.append("Stock attuale: ").append(product.get("current_stock")).append(" unità\n");
                            analisiProdotto.append("Vendite totali: ").append(product.get("total_sold")).append(" unità\n");
                            analisiProdotto.append("Velocità vendita: ").append(product.get("daily_sales_rate")).append(" unità/giorno\n");
                            analisiProdotto.append("Giorni di copertura: ").append(String.format("%.1f", product.get("days_of_stock"))).append(" giorni\n");
                            analisiProdotto.append("Prezzo unitario: €").append(String.format("%.2f", product.get("price"))).append("\n\n");

                            // ========== ANALISI E SPIEGAZIONE ==========
                            String explanation = (String) product.get("explanation");
                            if (explanation != null && !explanation.isEmpty()) {
                                analisiProdotto.append("🔍 ANALISI:\n");
                                analisiProdotto.append("─".repeat(80)).append("\n");
                                analisiProdotto.append(explanation).append("\n\n");
                            }

                            // ========== RACCOMANDAZIONE ==========
                            Integer suggestedQty = (Integer) product.get("suggested_order_quantity");
                            String stockStatus = (String) product.get("stock_status");
                            String recommendation = (String) product.get("recommendation");

                            analisiProdotto.append("💡 RACCOMANDAZIONE:\n");
                            analisiProdotto.append("─".repeat(80)).append("\n");

                            if (recommendation != null && !recommendation.isEmpty()) {
                                analisiProdotto.append(recommendation).append("\n");
                            } else if (suggestedQty != null && suggestedQty > 0) {
                                analisiProdotto.append("Ordina ").append(suggestedQty).append(" unità\n");
                            } else {
                                analisiProdotto.append("✅ Stock sufficiente - Non ordinare al momento\n");
                            }

                            analisiProdotto.append("Status: ").append(stockStatus != null ? stockStatus : "N/A").append("\n");
                            analisiProdotto.append("Urgenza: ").append(urgency).append("\n\n");

                            // ========== PREVISIONI FINANZIARIE SPECIFICHE ==========
                            if (suggestedQty != null && suggestedQty > 0) {
                                Number price = (Number) product.get("price");
                                Number totalRevenue = (Number) product.get("total_revenue");

                                if (price != null) {
                                    double investimento = suggestedQty * price.doubleValue();
                                    double revenueAtteso = investimento * 1.3; // Margine 30%
                                    double profitto = revenueAtteso - investimento;

                                    analisiProdotto.append("💰 PREVISIONI FINANZIARIE:\n");
                                    analisiProdotto.append("─".repeat(80)).append("\n");
                                    analisiProdotto.append("Investimento necessario: €").append(String.format("%,.2f", investimento)).append("\n");
                                    analisiProdotto.append("Revenue atteso (margine 30%): €").append(String.format("%,.2f", revenueAtteso)).append("\n");
                                    analisiProdotto.append("Profitto stimato: €").append(String.format("%,.2f", profitto)).append("\n");

                                    if (totalRevenue != null) {
                                        analisiProdotto.append("Revenue storico totale: €").append(String.format("%,.2f", totalRevenue.doubleValue())).append("\n");
                                    }

                                    // ROI
                                    double roi = (profitto / investimento) * 100;
                                    analisiProdotto.append("ROI atteso: ").append(String.format("%.1f", roi)).append("%\n");
                                }
                            } else {
                                analisiProdotto.append("💰 PREVISIONI FINANZIARIE:\n");
                                analisiProdotto.append("─".repeat(80)).append("\n");
                                analisiProdotto.append("Nessun investimento necessario al momento.\n");
                                analisiProdotto.append("Stock attuale sufficiente per la domanda corrente.\n");
                            }

                            // Aggiungi alla lista
                            raccomandazioniList.add(analisiProdotto.toString());
                        });

            } else {
                raccomandazioniList.add("Nessun prodotto disponibile per l'analisi.");
            }

            dto.setRaccomandazioni(raccomandazioniList);

            // ========== PREVISIONI TOTALI ==========
            StringBuilder previsioniTotali = new StringBuilder();
            previsioniTotali.append("💰 PREVISIONI FINANZIARIE TOTALI\n\n");

            if (products != null && !products.isEmpty()) {
                double totaleInvestimento = 0;
                double totaleRevenue = 0;

                for (Map<String, Object> product : products) {
                    Number qty = (Number) product.get("suggested_order_quantity");
                    Number price = (Number) product.get("price");

                    if (qty != null && price != null && qty.intValue() > 0) {
                        double investimento = qty.doubleValue() * price.doubleValue();
                        totaleInvestimento += investimento;
                        totaleRevenue += investimento * 1.3;
                    }
                }

                previsioniTotali.append("Investimento totale consigliato:\n");
                previsioniTotali.append("€ ").append(String.format("%,.2f", totaleInvestimento)).append("\n\n");

                previsioniTotali.append("Revenue totale atteso (margine 30%):\n");
                previsioniTotali.append("€ ").append(String.format("%,.2f", totaleRevenue)).append("\n\n");

                previsioniTotali.append("Profitto totale stimato:\n");
                previsioniTotali.append("€ ").append(String.format("%,.2f", totaleRevenue - totaleInvestimento)).append("\n\n");

                // Riepilogo urgenze
                long critici = products.stream()
                        .filter(p -> "ALTA".equals(p.get("urgency")))
                        .count();
                long medi = products.stream()
                        .filter(p -> "MEDIA".equals(p.get("urgency")))
                        .count();

                previsioniTotali.append("📊 RIEPILOGO ORDINI:\n");
                if (critici > 0) {
                    previsioniTotali.append("🔴 ").append(critici).append(" prodotti CRITICI da ordinare subito\n");
                }
                if (medi > 0) {
                    previsioniTotali.append("🟡 ").append(medi).append(" prodotti da monitorare (attenzione media)\n");
                }

                previsioniTotali.append("\n📈 TREND:\n");
                long bestSellers = products.stream()
                        .filter(p -> {
                            Number rate = (Number) p.get("daily_sales_rate");
                            return rate != null && rate.doubleValue() > 3;
                        })
                        .count();

                if (bestSellers > 0) {
                    previsioniTotali.append("⚡ ").append(bestSellers).append(" best sellers ad alta rotazione\n");
                }

                previsioniTotali.append("• Analisi basata su dati storici reali\n");
                previsioniTotali.append("• Raccomandazioni aggiornate in tempo reale\n");

            } else {
                previsioniTotali.append("Dati insufficienti per generare previsioni.");
            }

            dto.setPrevisioni(previsioniTotali.toString());

        } catch (Exception e) {
            System.err.println("❌ Errore mappatura: " + e.getMessage());
            e.printStackTrace();

            dto.setInsights("Errore durante l'elaborazione dei dati");
            dto.setRaccomandazioni(Collections.singletonList("Impossibile generare raccomandazioni"));
            dto.setPrevisioni("Impossibile generare previsioni");
        }

        return dto;
    }
}
