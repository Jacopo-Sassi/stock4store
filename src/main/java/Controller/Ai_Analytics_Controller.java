package Controller;

import org.example.pw_be.api.AiAnalyticsApi;
import org.example.pw_be.model.dto.AIAnalyticsResponse;
import org.springframework.http.ResponseEntity;

public class Ai_Analytics_Controller implements AiAnalyticsApi {
    @Override
    public ResponseEntity<AIAnalyticsResponse> _getAIAnalytics() {
        return null;
    }
}
