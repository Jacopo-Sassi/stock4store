package pw_be.Controller;


import org.example.pw_be.api.DashboardApi;
import org.example.pw_be.model.dto.DashboardResponseDto;
import org.springframework.http.ResponseEntity;

public class Dashboard_Controller implements DashboardApi {
    @Override
    public ResponseEntity<DashboardResponseDto> _getDashboard() {
        return null;
    }
}
