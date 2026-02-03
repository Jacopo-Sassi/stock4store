package Controller;

import org.example.pw_be.api.AnalyticsApi;
import org.example.pw_be.model.dto.AnalyticsResponse;
import org.springframework.http.ResponseEntity;

public class Analytics_Controller implements AnalyticsApi{
    @Override
    public ResponseEntity<AnalyticsResponse> _getAnalyticsArticoli() {
        return null;
    }
}
