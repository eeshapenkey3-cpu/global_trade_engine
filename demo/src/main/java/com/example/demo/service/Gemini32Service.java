package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Service
public class Gemini32Service{
    @Value("${gemini.api.key}")
    private String apiKey;

    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public String askGemini32(String prompt){
        Map<String, Object> requestBody = Map.of(
                "model","gemini-3.8-flash",
                "input", prompt
        );

        try{
            String rawJson = restClient.post()
                    .uri(apiUrl)
                    .header("x-goog-api-key",apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode root = jsonMapper.readTree(rawJson);

            JsonNode textNode = root.path("interaction").path("output_text");
            if(!textNode.isMissingNode() && !textNode.asText().isEmpty()){
                return textNode.asText();
            }

            JsonNode altNode = root.path("output_text");
        }
    }
}