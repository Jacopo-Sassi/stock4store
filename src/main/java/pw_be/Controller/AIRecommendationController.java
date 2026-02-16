package pw_be.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pw_be.Service.AIRecommendationService;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIRecommendationController {

    @Autowired
    private AIRecommendationService aiRecommendationService;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            return ResponseEntity.ok(aiRecommendationService.healthCheck());
        } catch (Exception e) {
            return ResponseEntity.status(503)
                    .body("AI service unavailable: " + e.getMessage());
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations() {
        try {
            return ResponseEntity.ok(aiRecommendationService.getRecommendations());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Error: " + e.getMessage());
        }
    }
}
