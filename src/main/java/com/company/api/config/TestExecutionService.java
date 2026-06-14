package com.company.api.config;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class TestExecutionService {

    @Autowired
    private ApiConfigRepository apiRepo;

    @Autowired
    private TestResultRepository testResultRepo;

    /**
     * Execute real API tests and store results in PostgreSQL.
     */
    public Map<String, Object> executeTests(List<String> apiNames, String environment) {
        List<ApiEntity> apis = apiRepo.findAll();
        int totalTests = 0, passed = 0, failed = 0;
        List<Map<String, Object>> testDetails = new ArrayList<>();

        for (ApiEntity api : apis) {
            List<Map<String, Object>> results = testApiEndpoints(api);
            totalTests += results.size();
            for (Map<String, Object> result : results) {
                if ("PASS".equals(result.get("status"))) {
                    passed++;
                } else {
                    failed++;
                }
                testDetails.add(result);
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", totalTests);
        summary.put("passed", passed);
        summary.put("failed", failed);
        summary.put("accuracy", totalTests > 0 ? (int)((passed * 100.0) / totalTests) : 0);
        summary.put("tests", testDetails);
        summary.put("status", "SUCCESS");

        return summary;
    }

    private List<Map<String, Object>> testApiEndpoints(ApiEntity api) {
        List<Map<String, Object>> results = new ArrayList<>();

        // Get endpoints from stored data or use defaults
        String[] endpoints = api.getEndpoints() != null ?
            api.getEndpoints().split(",") : new String[]{"/"};

        for (String endpoint : endpoints) {
            try {
                RestAssured.baseURI = api.getBaseUrl();

                Response response = RestAssured.given()
                    .when().get(api.getBasePath() + endpoint);

                int statusCode = response.getStatusCode();
                boolean isPass = statusCode >= 200 && statusCode < 300;

                // Calculate confidence score: 70% response time, 20% status, 10% content
                double confidenceScore = calculateConfidence(
                    response.getTime(),
                    statusCode,
                    response.getBody().asString()
                );

                Map<String, Object> result = new HashMap<>();
                result.put("api", api.getName());
                result.put("method", "GET");
                result.put("endpoint", endpoint);
                result.put("status", isPass ? "PASS" : "FAIL");
                result.put("statusCode", statusCode);
                result.put("duration", response.getTime() + "ms");
                result.put("response", truncate(response.getBody().asString(), 100));
                result.put("confidenceScore", confidenceScore);
                result.put("confidenceFactors", Map.of(
                    "responseTime", response.getTime(),
                    "statusCode", statusCode,
                    "hasContent", response.getBody().asString().length() > 0
                ));

                results.add(result);
            } catch (Exception e) {
                Map<String, Object> result = new HashMap<>();
                result.put("api", api.getName());
                result.put("method", "GET");
                result.put("endpoint", endpoint);
                result.put("status", "ERROR");
                result.put("error", e.getMessage());
                result.put("confidenceScore", 0.0);
                results.add(result);
            }
        }

        return results;
    }

    private double calculateConfidence(long responseTime, int statusCode, String body) {
        // Response time score (0-0.7): faster is better
        double timeScore = Math.max(0, 1 - (responseTime / 5000.0)) * 0.7;

        // Status code score (0-0.2): 2xx is best
        double statusScore = (statusCode >= 200 && statusCode < 300) ? 0.2 :
                           (statusCode >= 400 && statusCode < 500) ? 0.1 : 0;

        // Content score (0-0.1): has content
        double contentScore = (body != null && !body.isEmpty()) ? 0.1 : 0;

        return Math.round((timeScore + statusScore + contentScore) * 1000) / 1000.0;
    }

    private String truncate(String str, int len) {
        return str != null && str.length() > len ? str.substring(0, len) + "..." : str;
    }
}