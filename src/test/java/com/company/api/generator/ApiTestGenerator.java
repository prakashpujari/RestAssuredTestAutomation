package com.company.api.generator;

import com.company.api.core.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * Automated API Test Generator
 * Generates and runs tests for any API endpoint
 */
public class ApiTestGenerator {

    private final ApiTestConfig config;
    private final List<TestResult> results = new ArrayList<>();

    public ApiTestGenerator(ApiTestConfig config) {
        this.config = config;
        setupEnvironment();
    }

    private void setupEnvironment() {
        // Set base URL
        System.setProperty("env.url", config.getEndpoint().replace("/api/v1/products", ""));
        ApiConfig.configure();
    }

    public List<TestResult> generateAndRunTests() {
        // Test 1: Valid request
        runTest("Valid Request", createValidRequest(), 200, 299);

        // Test 2: Invalid request body
        runTest("Invalid Request Body", createInvalidRequest(), 400, 499);

        // Test 3: Missing authentication
        runTest("Missing Auth", createUnauthorizedRequest(), 401, 499);

        // Test 4: Not found (for GET/PUT/DELETE)
        if (!"POST".equals(config.getMethod())) {
            runTest("Not Found", createNotFoundRequest(), 404, 499);
        }

        return results;
    }

    private void runTest(String testName, RequestBuilder request, int minStatus, int maxStatus) {
        try {
            Response response = request.send();
            int statusCode = response.getStatusCode();

            String issue = "";
            String status = "PASS";

            if (statusCode < minStatus || statusCode > maxStatus) {
                status = "FAIL";
                issue = String.format("Expected status %d-%d, got %d", minStatus, maxStatus, statusCode);
            }

            results.add(new TestResult(
                    config.getEndpoint(),
                    config.getMethod(),
                    request.getRequestBody(),
                    statusCode,
                    response.getBody().asString(),
                    status,
                    issue
            ));
        } catch (Exception e) {
            results.add(new TestResult(
                    config.getEndpoint(),
                    config.getMethod(),
                    request.getRequestBody(),
                    0,
                    "",
                    "ERROR",
                    e.getMessage()
            ));
        }
    }

    private RequestBuilder createValidRequest() {
        return new RequestBuilder() {
            @Override
            public Response send() {
                return buildRequest(config.getRequestBody()).when().request(
                        config.getMethod(), extractPath(config.getEndpoint()));
            }

            @Override
            public String getRequestBody() {
                return config.getRequestBody();
            }
        };
    }

    private RequestBuilder createInvalidRequest() {
        return new RequestBuilder() {
            @Override
            public Response send() {
                return buildRequest("{}").when().request(
                        config.getMethod(), extractPath(config.getEndpoint()));
            }

            @Override
            public String getRequestBody() {
                return "{}";
            }
        };
    }

    private RequestBuilder createUnauthorizedRequest() {
        return new RequestBuilder() {
            @Override
            public Response send() {
                return buildRequestWithoutAuth(config.getRequestBody()).when().request(
                        config.getMethod(), extractPath(config.getEndpoint()));
            }

            @Override
            public String getRequestBody() {
                return config.getRequestBody();
            }
        };
    }

    private RequestBuilder createNotFoundRequest() {
        return new RequestBuilder() {
            @Override
            public Response send() {
                String pathWithId = config.getEndpoint() + "/99999";
                return buildRequest(config.getRequestBody()).when().request(
                        config.getMethod(), extractPath(pathWithId));
            }

            @Override
            public String getRequestBody() {
                return config.getRequestBody();
            }
        };
    }

    private io.restassured.specification.RequestSpecification buildRequest(String body) {
        io.restassured.specification.RequestSpecification req = given()
                .header("client_id", "test-client")
                .header("client_secret", "test-secret")
                .contentType(ContentType.JSON);

        if (body != null && !body.isEmpty()) {
            req.body(body);
        }

        return req;
    }

    private io.restassured.specification.RequestSpecification buildRequestWithoutAuth(String body) {
        io.restassured.specification.RequestSpecification req = given()
                .contentType(ContentType.JSON);

        if (body != null && !body.isEmpty()) {
            req.body(body);
        }

        return req;
    }

    private String extractPath(String endpoint) {
        return endpoint.replaceFirst("^https?://[^/]+", "");
    }

    public List<TestResult> getResults() {
        return results;
    }

    interface RequestBuilder {
        Response send();
        String getRequestBody();
    }
}