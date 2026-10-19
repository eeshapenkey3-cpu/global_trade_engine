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

    private static final String MODEL = "gemini-3.8-flash";
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public String askGemini32(String prompt){
        String key = (apiKey == null) ? "" : apiKey.trim();

        Map<String, Object> requestBody = Map.of(
                "model","gemini-3.8-flash",
                "input", prompt
        );

        try{
            String rawJson = restClient.post()
                    .uri(API_URL)
                    .header("x-goog-api-key",key)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            JsonNode parts = jsonMapper.readTree(rawJson).path("candidates").path(0).path("content").path("parts");

            StringBuilder answer = new StringBuilder();
            for(JsonNode part : parts){
                if(part.has("text")){
                    answer.append(part.get("text").asText());
                }
            }
            return answer.length() > 0
                    ? answer.toString()
                    : "AI responded, but in unexpected format: "+rawJson;
        }catch(Exception e){
            return "AI assistant is temporarily unavailable: "+e.getMessage();
        }
    }
}