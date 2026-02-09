package com.example.gemini.controller;

import com.example.gemini.model.GeminiModel;
import com.example.gemini.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow all origins for simplicity, tighten in production
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping
    public ResponseEntity<GeminiModel> createResponse(@RequestBody GeminiModel geminiModel) {
        return ResponseEntity.ok(geminiService.saveResponse(geminiModel));
    }

    @GetMapping
    public ResponseEntity<List<GeminiModel>> getAllResponses() {
        return ResponseEntity.ok(geminiService.getAllResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeminiModel> getResponseById(@PathVariable String id) {
        return geminiService.getResponseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponse(@PathVariable String id) {
        geminiService.deleteResponse(id);
        return ResponseEntity.noContent().build();
    }
}
