package pw_be.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticoloAIDto {

    // Header
    private Integer productNumber;
    private String urgencyIcon;

    // Dettagli base
    private String productName;
    private String productId;
    private String category;

    // Stock e vendite
    private Integer currentStock;
    private Integer totalSold;
    private Double dailySalesRate;
    private Double daysOfStock;
    private Double price;

    // Analisi
    private String analysis;
    private Boolean analyzedWithAI;

    // Raccomandazione
    private String recommendation;
    private Integer suggestedOrderQuantity;
    private String stockStatus;
    private String urgency;

    // Previsioni finanziarie
    private Double investimento;
    private Double revenueAtteso;
    private Double profittoStimato;
    private Double revenueStorico;
    private Double roiAtteso;
}
