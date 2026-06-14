package com.company.api.core;

import com.company.api.config.ApiDefinitionLoader;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic API test runner that executes tests based on configuration.
 * Central point for all API test execution with environment awareness.
 */
public class ApiTestRunner {

    private final ApiDefinitionLoader loader = ApiDefinitionLoader.getInstance();
    private final String environment;
    private final Map<String, String> defaultHeaders;

    public ApiTestRunner(String environment) {
        this.environment = environment;
        this.defaultHeaders = loadDefaultHeaders();
    }

    private Map<String, String> loadDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> envConfig = loader.getEnvironmentConfig(environment);

        if (envConfig != null) {
            String clientId = loader.resolveValue((String) envConfig.get("client_id"));
            String clientSecret = loader.resolveValue((String) envConfig.get("client_secret"));

            if (clientId != null) headers.put("client_id", clientId);
            if (clientSecret != null) headers.put("client_secret", clientSecret);
        }
        return headers;
    }

    /**
     * Creates a base request specification with environment config.
     */
    public RequestSpecification createBaseRequest() {
        Map<String, String> envConfig = loader.getEnvironmentConfig(environment);

        if (envConfig != null && envConfig.get("baseUrl") != null) {
            String baseUrl = loader.resolveValue(envConfig.get("baseUrl"));
            RestAssured.baseURI = baseUrl;
        }

        RequestSpecification spec = RestAssured.given();

        // Add default headers
        defaultHeaders.forEach((key, value) -> spec.header(key, value));

        // Add correlation ID if configured
        spec.header("correlation_id", String.valueOf(System.currentTimeMillis()));

        return spec;
    }

    /**
     * Executes a GET request and validates status.
     */
    public void executeGet(String path, int expectedStatus, Map<String, String> pathParams) {
        RequestSpecification request = createBaseRequest();

        if (pathParams != null) {
            pathParams.forEach((key, value) -> request.pathParam(key, value));
        }

        request.when()
               .get(path)
               .then()
               .statusCode(expectedStatus);
    }

    /**
     * Executes a POST request and validates status.
     */
    public void executePost(String path, int expectedStatus, Object body) {
        RequestSpecification request = createBaseRequest();
        request.contentType("application/json").body(body);

        request.when()
               .post(path)
               .then()
               .statusCode(expectedStatus);
    }

    /**
     * Executes a PUT request and validates status.
     */
    public void executePut(String path, int expectedStatus, Object body, Map<String, String> pathParams) {
        RequestSpecification request = createBaseRequest();
        request.contentType("application/json").body(body);

        if (pathParams != null) {
            pathParams.forEach((key, value) -> request.pathParam(key, value));
        }

        request.when()
               .put(path)
               .then()
               .statusCode(expectedStatus);
    }

    /**
     * Executes a DELETE request and validates status.
     */
    public void executeDelete(String path, int expectedStatus, Map<String, String> pathParams) {
        RequestSpecification request = createBaseRequest();

        if (pathParams != null) {
            pathParams.forEach((key, value) -> request.pathParam(key, value));
        }

        request.when()
               .delete(path)
               .then()
               .statusCode(expectedStatus);
    }

    /**
     * Runs all tests for a specific API module.
     */
    public void runAllTestsForModule(String moduleName) {
        // Implementation would iterate through yaml configs and run tests
        System.out.println("Running all tests for module: " + moduleName);
    }
}