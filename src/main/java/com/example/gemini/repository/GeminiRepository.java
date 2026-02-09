package com.example.gemini.repository;

import com.example.gemini.model.GeminiModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeminiRepository extends MongoRepository<GeminiModel, String> {
}
