package com.example.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.unbescape.json.JsonEscape.escapeJson;

@Service
public class Gemini32Service{
    @Value("${gemini.api.key}")
    private String apiKey;

    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    //constructor

    public Gemini32Service(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.restClient = RestClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String getAiResponse(String userMessage){
        try{
            String jsonPayLoad = """
                    {
                        "contents": [{
                            "parts": [{"text": "%s"}]
                        }]
                    }
                    """.formatted(escapeJson(userMessage));

        String rawJsonResponse = restClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header("content-type","application/json")
                .body(jsonPayLoad)
                .retrieve()
                .body(String.class);

                JsonNode rootNode = objectMapper.readTree(rawJsonResponse);

                String aiText = rootNode.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text")
                        .asText();
                return aiText;
        }catch(Exception e){
            return "error calling gemini api: "+e.getMessage();
        }
    }

    private String escapeJson(String input){
        if(input == null)return "";
        return input.replace("\\","\\\\")
                .replace("\"","\\\"")
                .replace("\n","\\n")
                .replace("\r","\\r");
    }
}