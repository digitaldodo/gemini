package com.example.gemini.service;

import com.example.gemini.model.GeminiModel;
import com.example.gemini.repository.GeminiRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class GeminiService {

    private final GeminiRepository geminiRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public GeminiModel saveResponse(GeminiModel geminiModel) {
        String requestContent = geminiModel.getRequest();

        // Construct the request payload for Gemini API
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", requestContent)))));

        // Call Gemini API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            String response = restTemplate.postForObject(
                    geminiApiUrl + geminiApiKey,
                    requestEntity,
                    String.class);

            // Extract the text from the response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            String extractedResponse = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            geminiModel.setResponse(extractedResponse);
        } catch (Exception e) {
            geminiModel.setResponse("Error processing Gemini response: " + e.getMessage());
        }

        return geminiRepository.save(geminiModel);
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
