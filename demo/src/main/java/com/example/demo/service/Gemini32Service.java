//package com.example.demo.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestClient;
//import org.springframework.http.MediaType;
//
//import java.util.List;
//import java.util.Map;
//
//@Service //what does this annotation do
//public class Gemini32Service {
//    @Value("${gemini.api.key}")
//    private String apiKey;
//
//    @Value("${gemini.api.url}")
//    private String apiUrl;
//
//    private final RestClient restClient;
//
//    public Gemini32Service() {
//        this.restClient = RestClient.create();
//    }
//
//    public String askGemini32(String prompt){
//        //json body gemini expects
//        Map<String, Object> requestBody = Map.of(
//                "contents", List.of(
//                        Map.of("parts",List.of(
//                                Map.of("text",prompt)
//                        ))
//                )
//        );
//        Map response = restClient.post()
//                .uri(apiUrl + "?key="+apiKey)
//                .contentType(MediaType.APPLICATION_JSON)
//                .body
//    }
//}
