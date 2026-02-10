package pw_be.Controller;

import org.example.pw_be.api.AnalyticsApi;
import org.example.pw_be.model.dto.AnalyticsResponseDto;
import org.springframework.http.ResponseEntity;

public class Analytics_Controller implements AnalyticsApi {
    @Override
    public ResponseEntity<AnalyticsResponseDto> _getAnalyticsArticoli() {
        return null;
    }
}
