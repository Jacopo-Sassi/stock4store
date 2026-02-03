package Controller;

import org.example.pw_be.api.DashboardApi;
import org.example.pw_be.model.dto.DashboardResponse;
import org.springframework.http.ResponseEntity;

public class Dashboard_Controller implements DashboardApi {
    @Override
    public ResponseEntity<DashboardResponse> _getDashboard() {
        return null;
    }
}
