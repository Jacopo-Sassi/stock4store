package pw_be.Controller;

import pw_be.api.AnalyticsApi;
import pw_be.model.dto.AnalyticsResponseDto;
import org.springframework.http.ResponseEntity;

public class Analytics_Controller implements AnalyticsApi{
    @Override
    public ResponseEntity<AnalyticsResponseDto> _getAnalyticsArticoli() {
        return null;
    }
}
