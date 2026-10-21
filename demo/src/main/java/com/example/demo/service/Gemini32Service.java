package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;
import java.net.URI;

@Service
public class Gemini32Service {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private static final String MODEL = "gemini-3.8-flash";
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";

    private final RestClient restClient = RestClient.create();

    public String askGemini32(String query) {
        Map<String, Object> textMap = Map.of("text",query);
        Map<String, Object> partsMap = Map.of("parts",List.of(textMap));
        Map<String, Object> requestBody = Map.of("contents", List.of(partsMap));

        String key = (apiKey == null) ? "" : apiKey.trim();
        String targetedUrl = API_URL + "?key=" + key.trim();

        try {
            Map responseMap = restClient.post()
                    .uri(URI.create(targetedUrl))
                    .header("authorization",null)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if(responseMap == null){
                return "error";
            }
            if(responseMap.containsKey("error")){
                Map<?,?> errorObj = (Map<?,?>) responseMap.get("error");
                return "API error " + errorObj.get("message");
            }

            List<?> candidates = (List<?>) responseMap.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Error: No text candidates returned. Raw response: " + responseMap.toString();
            }

            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            if (content == null) {
                return "Error: Candidate content block missing.";
            }

            List<?> parts = (List<?>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "Error: Content parts block missing.";
            }

            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
            return (String) firstPart.get("text");
        } catch (Exception e) {
            return "HTTP request failed: " + e.getMessage();
        }
    }
}
