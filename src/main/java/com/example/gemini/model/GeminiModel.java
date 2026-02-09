package com.example.gemini.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gemini_responses")
public class GeminiModel {

    @Id
    private String id;
    private String request;
    private String response;

    public GeminiModel() {
    }

    public GeminiModel(String id, String request, String response) {
        this.id = id;
        this.request = request;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
