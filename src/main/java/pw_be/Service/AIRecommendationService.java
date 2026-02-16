package pw_be.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AIRecommendationService {

    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate;

    public AIRecommendationService() {
        this.restTemplate = new RestTemplate();
    }

    public Object getRecommendations() {
        String url = aiServiceUrl + "/api/recommendations";
        return restTemplate.getForObject(url, Object.class);
    }

    public Object healthCheck() {
        String url = aiServiceUrl + "/api/health";
        return restTemplate.getForObject(url, Object.class);
    }
}
