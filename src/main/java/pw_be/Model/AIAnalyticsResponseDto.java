package pw_be.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pw_be.Model.ArticoloAIDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalyticsResponseDto {

    private String insights;
    private List<ArticoloAIDto> raccomandazioni;
    private PrevisioniDto previsioni;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrevisioniDto {
        private Double investimentoTotale;
        private Double revenueTotale;
        private Double profittoTotale;
        private Integer prodottiCritici;
        private Integer prodottiMedia;
        private Integer bestSellers;
    }
}
