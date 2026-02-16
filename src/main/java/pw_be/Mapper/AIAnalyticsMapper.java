package pw_be.Mapper;

import org.example.pw_be.model.dto.AIAnalyticsResponseDto;
import org.example.pw_be.model.dto.ArticoloAIDtoDto;
import org.example.pw_be.model.dto.PrevisioniDtoDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AIAnalyticsMapper {

    public AIAnalyticsResponseDto mapToDto(Map<String, Object> pythonResponse) {
        AIAnalyticsResponseDto dto = new AIAnalyticsResponseDto();

        try {
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

            // ========== RACCOMANDAZIONI (Lista ArticoloAIDto) ==========
            List<ArticoloAIDtoDto> raccomandazioni = new ArrayList<>();

            if (products != null && !products.isEmpty()) {
                int[] counter = {1};

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
                        .forEach(p -> {
                            ArticoloAIDtoDto a = new ArticoloAIDtoDto();

                            a.setProductNumber(counter[0]++);

                            String urgency = (String) p.get("urgency");
                            a.setUrgencyIcon("ALTA".equals(urgency) ? "🔴" :
                                    "MEDIA".equals(urgency) ? "🟡" :
                                            "BASSA".equals(urgency) ? "🟢" : "⚪");

                            a.setProductName((String) p.get("product_name"));
                            a.setProductId((String) p.get("product_id"));
                            a.setCategory((String) p.get("category"));
                            a.setCurrentStock((Integer) p.get("current_stock"));
                            a.setTotalSold((Integer) p.get("total_sold"));

                            Number salesRate = (Number) p.get("daily_sales_rate");
                            a.setDailySalesRate(salesRate != null ? salesRate.doubleValue() : 0.0);

                            Number daysStock = (Number) p.get("days_of_stock");
                            a.setDaysOfStock(daysStock != null ? daysStock.doubleValue() : 0.0);

                            Number price = (Number) p.get("price");
                            a.setPrice(price != null ? price.doubleValue() : 0.0);

                            a.setAnalysis((String) p.get("explanation"));
                            a.setAnalyzedWithAI((Boolean) p.getOrDefault("analyzed_with_ai", false));
                            a.setRecommendation((String) p.get("recommendation"));
                            a.setSuggestedOrderQuantity((Integer) p.get("suggested_order_quantity"));
                            a.setStockStatus((String) p.get("stock_status"));
                            a.setUrgency(urgency);

                            // Previsioni finanziarie
                            Integer qty = a.getSuggestedOrderQuantity();
                            if (qty != null && qty > 0 && a.getPrice() != null && a.getPrice() > 0) {
                                double inv = qty * a.getPrice();
                                double rev = inv * 1.3;
                                double prof = rev - inv;

                                a.setInvestimento(inv);
                                a.setRevenueAtteso(rev);
                                a.setProfittoStimato(prof);
                                a.setRoiAtteso((prof / inv) * 100);

                                Number totalRevenue = (Number) p.get("total_revenue");
                                if (totalRevenue != null) {
                                    a.setRevenueStorico(totalRevenue.doubleValue());
                                }
                            } else {
                                a.setInvestimento(0.0);
                                a.setRevenueAtteso(0.0);
                                a.setProfittoStimato(0.0);
                                a.setRoiAtteso(0.0);
                            }

                            raccomandazioni.add(a);
                        });
            }

            dto.setRaccomandazioni(raccomandazioni);

            // ========== PREVISIONI (Oggetto strutturato) ==========
            PrevisioniDtoDto previsioni = new PrevisioniDtoDto();

            if (products != null && !products.isEmpty()) {
                double totInv = 0;
                double totRev = 0;

                for (Map<String, Object> p : products) {
                    Number qty = (Number) p.get("suggested_order_quantity");
                    Number price = (Number) p.get("price");

                    if (qty != null && price != null && qty.intValue() > 0) {
                        double inv = qty.doubleValue() * price.doubleValue();
                        totInv += inv;
                        totRev += inv * 1.3;
                    }
                }

                long critici = products.stream()
                        .filter(p -> "ALTA".equals(p.get("urgency")))
                        .count();

                long medi = products.stream()
                        .filter(p -> "MEDIA".equals(p.get("urgency")))
                        .count();

                long best = products.stream()
                        .filter(p -> {
                            Number rate = (Number) p.get("daily_sales_rate");
                            return rate != null && rate.doubleValue() > 3;
                        })
                        .count();

                previsioni.setInvestimentoTotale(totInv);
                previsioni.setRevenueTotale(totRev);
                previsioni.setProfittoTotale(totRev - totInv);
                previsioni.setProdottiCritici((int) critici);
                previsioni.setProdottiMedia((int) medi);
                previsioni.setBestSellers((int) best);
            } else {
                previsioni.setInvestimentoTotale(0.0);
                previsioni.setRevenueTotale(0.0);
                previsioni.setProfittoTotale(0.0);
                previsioni.setProdottiCritici(0);
                previsioni.setProdottiMedia(0);
                previsioni.setBestSellers(0);
            }

            dto.setPrevisioni(previsioni);

        } catch (Exception e) {
            System.err.println("❌ Errore mappatura: " + e.getMessage());
            e.printStackTrace();

            dto.setInsights("Errore durante l'elaborazione dei dati");
            dto.setRaccomandazioni(new ArrayList<>());

            PrevisioniDtoDto errorPrevisioni = new PrevisioniDtoDto();
            errorPrevisioni.setInvestimentoTotale(0.0);
            errorPrevisioni.setRevenueTotale(0.0);
            errorPrevisioni.setProfittoTotale(0.0);
            errorPrevisioni.setProdottiCritici(0);
            errorPrevisioni.setProdottiMedia(0);
            errorPrevisioni.setBestSellers(0);
            dto.setPrevisioni(errorPrevisioni);
        }

        return dto;
    }
}
