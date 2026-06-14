package com.company.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.List;

/**
 * Service for calling Groq LLM to generate a TestDefinition JSON.
 * Expects a prompt containing description, sample request/response and optional instructions.
 */
public class GroqService {
    private static final String GROQ_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "gemma-7b-it"; // can be adjusted if needed
    private final String apiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GroqService() {
        this.apiKey = System.getenv("GROQ_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IllegalStateException("GROQ_API_KEY environment variable not set");
        }
    }

    /**
     * Sends a request to Groq and returns the generated TestDefinition JSON string.
     */
    public String generateTest(String description, String sampleRequest, String sampleResponse, String instructions)
            throws IOException, InterruptedException {
        // Build the user message
        String userMessage = "Generate a Rest Assured test definition JSON.\n" +
                "Description: " + description + "\n" +
                "Sample Request: " + sampleRequest + "\n" +
                "Sample Response: " + sampleResponse + "\n" +
                (instructions != null && !instructions.isBlank() ? "Instructions: " + instructions + "\n" : "");

        Map<String, Object> payload = Map.of(
                "model", MODEL,
                "messages", List.of(Map.of("role", "user", "content", userMessage)),
                "temperature", 0.7
        );
        String requestBody = mapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Groq API error: HTTP " + response.statusCode() + " - " + response.body());
        }
        // Extract the assistant's content (simplified – assumes standard OpenAI compatible response)
        Map<String, Object> respMap = mapper.readValue(response.body(), Map.class);
        Object choicesObj = respMap.get("choices");
        if (choicesObj instanceof java.util.List) {
            java.util.List<?> list = (java.util.List<?>) choicesObj;
            if (!list.isEmpty()) {
                Object first = list.get(0);
                if (first instanceof java.util.Map) {
                    java.util.Map<?, ?> choiceMap = (java.util.Map<?, ?>) first;
                    Object messageObj = choiceMap.get("message");
                    if (messageObj instanceof java.util.Map) {
                        java.util.Map<?, ?> msgMap = (java.util.Map<?, ?>) messageObj;
                        Object content = msgMap.get("content");
                        if (content != null) {
                            return content.toString().trim();
                        }
                    }
                }
            }
        }
        throw new IOException("Unexpected Groq response format");
    }
}
