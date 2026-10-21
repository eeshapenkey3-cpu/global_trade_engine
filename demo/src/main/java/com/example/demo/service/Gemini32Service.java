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

    private static final String MODEL = "gemini-3.8-flash";
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";

    private final RestClient restClient = RestClient.create();

    public String askGemini32(String query) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", query)))
                )
        );

        String key = (apiKey == null) ? "" : apiKey.trim();

        try {
            Map<?, ?> responseMap = restClient.post()
                    .uri(API_URL)
                    .header("x-goog-api-key", key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            if (responseMap == null) {
                return "Error: empty response.";
            }

            List<?> candidates = (List<?>) responseMap.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Error: no candidates returned. Raw response: " + responseMap;
            }

            Map<?, ?> content = (Map<?, ?>) ((Map<?, ?>) candidates.get(0)).get("content");
            if (content == null) {
                return "Error: candidate content missing.";
            }

            List<?> parts = (List<?>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                return "Error: content parts missing.";
            }

            StringBuilder answer = new StringBuilder();
            for (Object p : parts) {
                Object text = ((Map<?, ?>) p).get("text");
                if (text != null) {
                    answer.append(text);
                }
            }
            return answer.length() > 0 ? answer.toString() : "Error: no text in response.";

        } catch (Exception e) {
            return "HTTP request failed: " + e.getMessage();
        }
    }
}