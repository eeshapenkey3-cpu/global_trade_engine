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

    private final RestClient restClient = RestClient.create();

    public String askGemini32(String prompt) {
        String key = (apiKey == null) ? "" : apiKey.trim();

        String API_URL =
                "https://googleapis.com" + MODEL + ":generateContent";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        try {
            Map responseMap = restClient.post()
                    .uri(URI.create(API_URL))
                    .header("x-goog-api-key", key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List candidates = (List) responseMap.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);

            return firstPart.get("text").toString();

        } catch (Exception e) {
            return "AI assistant is temporarily unavailable: " + e.getMessage();
        }
    }
}
