package pw_be.Controller;

import org.example.pw_be.api.AiAnalyticsApi;
import org.example.pw_be.model.dto.AIAnalyticsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pw_be.Service.AIRecommendationService;

@RestController
public class AIRecommendationController implements AiAnalyticsApi {

    @Autowired
    private AIRecommendationService aiRecommendationService;

    @Override
    public ResponseEntity<AIAnalyticsResponseDto> _getAIAnalytics() {
        try {
            System.out.println("📞 Richiesta AI Analytics ricevuta...");

            AIAnalyticsResponseDto response = aiRecommendationService.getAIAnalytics();

            System.out.println("✅ Analytics generato con successo");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Errore AI Analytics: " + e.getMessage());
            e.printStackTrace();

            AIAnalyticsResponseDto errorResponse = new AIAnalyticsResponseDto();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
