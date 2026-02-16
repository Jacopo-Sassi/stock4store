package pw_be.Service;

import org.example.pw_be.model.dto.AIAnalyticsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import pw_be.Mapper.AIAnalyticsMapper;

import java.util.Map;

@Service
public class AIRecommendationService {

    @Value("${ai.service.url:http://localhost:5000}")
    private String aiServiceUrl;

    private final RestTemplate restTemplate;
    private final AIAnalyticsMapper mapper;

    @Autowired
    public AIRecommendationService(AIAnalyticsMapper mapper) {
        this.restTemplate = new RestTemplate();
        this.mapper = mapper;
    }

    public AIAnalyticsResponseDto getAIAnalytics() {
        try {
            String url = aiServiceUrl + "/api/recommendations";

            System.out.println("🔍 Chiamando Python AI: " + url);

            // Ottieni risposta come Map
            Map<String, Object> pythonResponse = restTemplate.getForObject(url, Map.class);

            if (pythonResponse == null) {
                throw new RuntimeException("Risposta vuota dal servizio AI");
            }

            System.out.println("📦 Risposta ricevuta, mappatura in corso...");

            // Usa il mapper personalizzato
            AIAnalyticsResponseDto dto = mapper.mapToDto(pythonResponse);

            System.out.println("✅ DTO generato con successo");

            return dto;

        } catch (Exception e) {
            System.err.println("❌ Errore: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get AI analytics: " + e.getMessage(), e);
        }
    }

    // ✅ Aggiungi questo metodo per il controller SSE
    public Map<String, Object> getAIAnalyticsRaw() {
        try {
            String url = aiServiceUrl + "/api/recommendations";
            System.out.println("🔍 Chiamando Python AI: " + url);

            Map<String, Object> pythonResponse = restTemplate.getForObject(url, Map.class);

            if (pythonResponse == null) {
                throw new RuntimeException("Risposta vuota dal servizio AI");
            }

            return pythonResponse;

        } catch (Exception e) {
            System.err.println("❌ Errore: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get AI analytics: " + e.getMessage(), e);
        }
    }

    public Object healthCheck() {
        String url = aiServiceUrl + "/api/health";
        return restTemplate.getForObject(url, Object.class);
    }
}
