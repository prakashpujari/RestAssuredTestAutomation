package com.company.api.tests;

import com.company.api.config.ApiDefinitionLoader;
import com.company.api.core.ApiTestRunner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Dynamic API test class that generates tests from YAML configuration.
 * Add all 100+ APIs to apis.yaml and they will be tested automatically.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DynamicApiTest {

    private ApiDefinitionLoader loader;
    private String environment;

    @BeforeAll
    void setUpAll() {
        environment = System.getProperty("test.env", "local");
        loader = ApiDefinitionLoader.getInstance();
        System.out.println("Running tests in environment: " + environment);
    }

    @TestFactory
    Stream<DynamicTest> generateTestsFromConfig() {
        List<DynamicTest> tests = new java.util.ArrayList<>();

        List<Map<String, Object>> apis = loader.getApiDefinitions();
        for (Map<String, Object> api : apis) {
            String apiName = (String) api.get("name");
            String basePath = (String) api.get("basePath");
            String apiBaseUrl = (String) api.get("baseUrl"); // Per-API base URL

            if (apiBaseUrl == null) {
                // Fall back to environment config
                apiBaseUrl = loader.getEnvironmentConfig(environment).get("baseUrl");
            }

            List<Map<String, Object>> endpoints = (List<Map<String, Object>>) api.get("endpoints");

            for (Map<String, Object> endpoint : endpoints) {
                String method = (String) endpoint.get("method");
                String path = (String) endpoint.get("path");
                List<Map<String, Object>> testCases = (List<Map<String, Object>>) endpoint.get("tests");

                for (Map<String, Object> testCase : testCases) {
                    String testName = (String) testCase.get("name");
                    Integer expectedStatus = (Integer) testCase.get("expectedStatus");
                    String testType = (String) testCase.get("type");
                    Map<String, Object> queryParamsObj = (Map<String, Object>) testCase.get("queryParams");
                    Map<String, Object> pathParamsObj = (Map<String, Object>) testCase.get("pathParams");

                    // Convert to String maps
                    Map<String, String> queryParams = convertToStringMap(queryParamsObj);
                    Map<String, String> pathParams = convertToStringMap(pathParamsObj);

                    String testDisplayName = String.format("%s - %s %s [%s]",
                            apiName, method.toUpperCase(), path, testName);

                    String finalApiBaseUrl = apiBaseUrl;
                    tests.add(dynamicTest(testDisplayName, () ->
                        executeDynamicTest(finalApiBaseUrl, basePath, method, path,
                                expectedStatus, testType, queryParams, pathParams)));
                }
            }
        }

        return tests.stream();
    }

    private Map<String, String> convertToStringMap(Map<String, Object> input) {
        if (input == null) return null;
        Map<String, String> result = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }
        return result;
    }

    private void executeDynamicTest(String baseUrl, String basePath, String method, String path,
                                   int expectedStatus, String testType, Map<String, String> queryParams,
                                   Map<String, String> pathParams) {
        // Replace path parameters if provided
        String resolvedPath = path;
        if (pathParams != null) {
            for (Map.Entry<String, String> entry : pathParams.entrySet()) {
                resolvedPath = resolvedPath.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        String fullPath = basePath + resolvedPath;

        // Configure base URI for this specific API
        if (baseUrl != null) {
            RestAssured.baseURI = baseUrl;
        }

        // Determine if we need to skip WireMock/Local execution for external APIs
        boolean isExternalApi = baseUrl != null && !baseUrl.contains("localhost") && !baseUrl.contains("wiremock");

        switch (method.toUpperCase()) {
            case "GET":
                var request = given();
                if (queryParams != null) {
                    queryParams.forEach((k, v) -> request.queryParam(k, v));
                }
                request.when()
                    .get(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            case "POST":
                given()
                    .contentType(ContentType.JSON)
                    .body(createPostBody(path))
                    .when()
                    .post(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            case "PUT":
                given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\": \"updated\"}")
                    .when()
                    .put(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            case "DELETE":
                given()
                    .when()
                    .delete(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }

    private String createPostBody(String path) {
        if (path.contains("/posts")) {
            return "{\"title\": \"Test Post\", \"body\": \"Test body\", \"userId\": 1}";
        } else if (path.contains("/users")) {
            return "{\"name\": \"Test User\", \"job\": \"Tester\"}";
        }
        return "{\"test\": true}";
    }
}