package com.company.api.tests;

import com.company.api.config.ApiDefinitionLoader;
import com.company.api.core.ApiTestRunner;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Dynamic API test class that generates tests from YAML configuration.
 * Add all 100+ APIs to apis.yaml and they will be tested automatically.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DynamicApiTest {

    private ApiDefinitionLoader loader;
    private ApiTestRunner runner;
    private String environment;

    @BeforeAll
    void setUpAll() {
        environment = System.getProperty("test.env", "local");
        loader = ApiDefinitionLoader.getInstance();
        runner = new ApiTestRunner(environment);
        System.out.println("Running tests in environment: " + environment);
    }

    @BeforeEach
    void setUp() {
        Map<String, String> envConfig = loader.getEnvironmentConfig(environment);
        if (envConfig != null && envConfig.get("baseUrl") != null) {
            String baseUrl = loader.resolveValue(envConfig.get("baseUrl"));
            RestAssured.baseURI = baseUrl;
        }
        // Apply common config (timeout, filters, etc.)
        com.company.api.core.ApiConfig.configure();
    }

    @TestFactory
    Stream<DynamicTest> generateTestsFromConfig() {
        List<DynamicTest> tests = new java.util.ArrayList<>();

        List<Map<String, Object>> apis = loader.getApiDefinitions();
        for (Map<String, Object> api : apis) {
            String apiName = (String) api.get("name");
            String basePath = (String) api.get("basePath");
            List<Map<String, Object>> endpoints = (List<Map<String, Object>>) api.get("endpoints");

            for (Map<String, Object> endpoint : endpoints) {
                String method = (String) endpoint.get("method");
                String path = (String) endpoint.get("path");
                List<Map<String, Object>> testCases = (List<Map<String, Object>>) endpoint.get("tests");

                for (Map<String, Object> testCase : testCases) {
                    String testName = (String) testCase.get("name");
                    Integer expectedStatus = (Integer) testCase.get("expectedStatus");
                    String testType = (String) testCase.get("type");

                    String testDisplayName = String.format("%s - %s %s [%s]",
                            apiName, method.toUpperCase(), basePath + path, testName);

                    tests.add(dynamicTest(testDisplayName, () ->
                        executeDynamicTest(apiName, basePath, method, path, expectedStatus, testType, testCase)));
                }
            }
        }

        return tests.stream();
    }

    private void executeDynamicTest(String apiName, String basePath, String method, String path,
                                   int expectedStatus, String testType, Map<String, Object> testCase) {
        String fullPath = basePath + path;

        switch (method.toUpperCase()) {
            case "GET":
                RestAssured.given()
                    .when()
                    .get(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            case "POST":
                RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body("{\"test\": true}") // Use test data builder
                    .when()
                    .post(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            case "PUT":
                RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body("{\"test\": true}")
                    .when()
                    .put(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            case "DELETE":
                RestAssured.given()
                    .when()
                    .delete(fullPath)
                    .then()
                    .statusCode(expectedStatus);
                break;

            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }
}