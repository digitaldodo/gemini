package com.example.gemini.service;

import com.example.gemini.model.GeminiModel;
import com.example.gemini.repository.GeminiRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GeminiService {

    private final GeminiRepository geminiRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiService(GeminiRepository geminiRepository) {
        this.geminiRepository = geminiRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public GeminiModel saveResponse(GeminiModel geminiModel) {
        String requestContent = geminiModel.getRequest();

        try {
            // Build request payload for OpenRouter API
            Map<String, Object> requestBody = Map.of(
                    "model", "google/gemini-2.0-flash-001",
                    "messages", List.of(
                            Map.of("role", "user", "content", requestContent)));

            // Set headers for OpenRouter
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("HTTP-Referer", "http://localhost:8080");
            headers.set("X-Title", "Gemini App");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Call OpenRouter API
            String apiResponse = restTemplate.postForObject(apiUrl, requestEntity, String.class);

            // Parse response to extract generated text
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            String generatedText = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            geminiModel.setResponse(generatedText);

        } catch (Exception e) {
            geminiModel.setResponse("Error: " + e.getMessage());
        }

        // ALWAYS save to MongoDB
        GeminiModel saved = geminiRepository.save(geminiModel);
        System.out.println("Saved to MongoDB: id=" + saved.getId() + ", request=" + saved.getRequest());
        return saved;
    }

    public List<GeminiModel> getAllResponses() {
        return geminiRepository.findAll();
    }

    public Optional<GeminiModel> getResponseById(String id) {
        return geminiRepository.findById(id);
    }

    public void deleteResponse(String id) {
        geminiRepository.deleteById(id);
    }
}
