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

    private final ApiDefinitionLoader loader = ApiDefinitionLoader.getInstance();

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
        Map<String, Object> envs = (Map<String, Object>) loader.getEnvironmentConfig("local");
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
            @RequestBody TestRunRequest request) {

        // This would trigger the test execution
        // Could use Jenkins API, GitHub Actions, or direct Maven invocation
        Map<String, Object> response = new HashMap<>();
        response.put("jobId", UUID.randomUUID().toString());
        response.put("status", "QUEUED");
        response.put("message", "Test run triggered for: " + String.join(", ", request.apis));
        response.put("environment", request.environment);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * Get test run status.
     */
    @GetMapping("/run/{jobId}")
    public ResponseEntity<Map<String, Object>> getTestRunStatus(@PathVariable String jobId) {
        Map<String, Object> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("status", "COMPLETED"); // or RUNNING, FAILED
        response.put("passed", 85);
        response.put("failed", 3);
        response.put("total", 88);

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