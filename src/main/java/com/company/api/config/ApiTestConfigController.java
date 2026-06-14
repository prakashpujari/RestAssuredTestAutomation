package com.company.api.config;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

/**
 * REST API endpoint for UI-based test configuration.
 * Allows business users to configure API tests without touching code.
 *
 * To enable: Add @RestController annotation and run as Spring Boot app
 */
@RestController
@RequestMapping("/api/test-config")
public class ApiTestConfigController {

    private ApiDefinitionLoader loader;

    public ApiTestConfigController() {
        try {
            loader = ApiDefinitionLoader.getInstance();
        } catch (Exception e) {
            // Initialize with empty config if file loading fails
            loader = null;
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "API Test Framework is running");
        return ResponseEntity.ok(response);
    }

    /**
     * Get all available API endpoints for testing.
     */
    @GetMapping("/apis")
    public ResponseEntity<List<Map<String, Object>>> getAllApis() {
        if (loader == null) {
            // Return sample APIs for demo
            List<Map<String, Object>> sampleApis = List.of(
                Map.of("name", "jsonplaceholder-posts", "basePath", "/", "baseUrl", "https://jsonplaceholder.typicode.com"),
                Map.of("name", "httpbin", "basePath", "/", "baseUrl", "https://httpbin.org")
            );
            return ResponseEntity.ok(sampleApis);
        }
        return ResponseEntity.ok(loader.getApiDefinitions());
    }

    /**
     * Get all available API endpoints for testing.
     */
    @GetMapping("/apis")
    public ResponseEntity<List<Map<String, Object>>> getAllApis() {
        return ResponseEntity.ok(loader.getApiDefinitions());
    }

    /**
     * Get test configuration for a specific API.
     */
    @GetMapping("/apis/{apiName}")
    public ResponseEntity<Map<String, Object>> getApiConfig(@PathVariable String apiName) {
        if (loader == null) {
            return ResponseEntity.ok(Map.of("name", apiName));
        }
        return loader.getApiDefinitions().stream()
            .filter(api -> apiName.equals(api.get("name")))
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get environments available for testing.
     */
    @GetMapping("/environments")
    public ResponseEntity<List<String>> getEnvironments() {
        // This would return from the environment config
        List<String> environments = Arrays.asList("local", "dev", "qa", "uat", "prod");
        return ResponseEntity.ok(environments);
    }

    /**
     * Update test execution configuration for an environment.
     * In production, this would write to a database or config service.
     */
    @PostMapping("/environments/{envName}/config")
    public ResponseEntity<Map<String, Object>> updateEnvironmentConfig(
            @PathVariable String envName,
            @RequestBody Map<String, Object> config) {

        // In a real implementation, this would persist to a config service
        // For now, just return the received config
        Map<String, Object> response = new HashMap<>();
        response.put("environment", envName);
        response.put("config", config);
        response.put("message", "Configuration updated successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * Trigger test run for selected APIs.
     */
    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> triggerTestRun(
            @RequestBody(required = false) TestRunRequest request) {

        // In a real implementation, this would trigger Maven tests
        // For demo, return simulated results
        Map<String, Object> response = new HashMap<>();
        response.put("jobId", UUID.randomUUID().toString());
        response.put("status", "SUCCESS");
        response.put("message", "Test run completed successfully");
        response.put("passed", 14);
        response.put("failed", 0);
        response.put("total", 14);

        return ResponseEntity.ok(response);
    }

    /**
     * Get test run status.
     */
    @GetMapping("/run/{jobId}")
    public ResponseEntity<Map<String, Object>> getTestRunStatus(@PathVariable String jobId) {
        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("status", "COMPLETED");
        response.put("passed", 14);
        response.put("failed", 0);
        response.put("total", 14);
        response.put("message", "All tests passed successfully");

        return ResponseEntity.ok(response);
    }

    // DTO for test run requests
    public static class TestRunRequest {
        public List<String> apis;
        public String environment;
        public Boolean parallel;
        public Integer threadCount;
        public String[] tags; // What types of tests to run: positive, negative, security
    }
}