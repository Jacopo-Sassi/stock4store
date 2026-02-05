package pw_be.Controller;

import pw_be.api.AiAnalyticsApi;
import pw_be.model.dto.AIAnalyticsResponseDto;
import org.springframework.http.ResponseEntity;

public class Ai_Analytics_Controller implements AiAnalyticsApi {
    @Override
    public ResponseEntity<AIAnalyticsResponseDto> _getAIAnalytics() {
        return null;
    }
}
