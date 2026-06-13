package com.company.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;
import java.util.UUID;

/**
 * Central configuration for API tests including common filters and settings.
 */
public class ApiConfig {

    /**
     * Applies common configuration to RestAssured requests.
     * Includes correlation ID, logging, headers, and retry logic.
     */
    public static void configure() {
        // Clear any existing configuration
        io.restassured.RestAssured.filters().clear();

        // Add correlation ID filter (generates UUID per request)
        io.restassured.RestAssured.filters().add(new CorrelationIdFilter());

        // Add logging filter
        io.restassured.RestAssured.filters().add(new LoggingFilter());

        // Add retry filter for flaky endpoints
        io.restassured.RestAssured.filters().add(new RetryFilter(3)); // 3 retries

        // Set base URL from environment resolver
        String baseUrl = EnvironmentResolver.getBaseUrl();
        io.restassured.RestAssured.baseURI = baseUrl;
        System.out.println("ApiConfig.configure(): Setting RestAssured baseURI to: " + baseUrl);

        // Set timeout (in milliseconds) - configurable via system property
        String timeoutStr = System.getProperty("restassured.timeout", "10000");
        long timeoutMillis = Long.parseLong(timeoutStr);
        io.restassured.RestAssured.config = io.restassured.config.RestAssuredConfig.newConfig()
                .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMillis)
                        .setParam("http.socket.timeout", timeoutMillis));

        // Enable logging of request and response if validation fails
        io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Resets RestAssured configuration to defaults.
     */
    public static void reset() {
        io.restassured.RestAssured.reset();
    }
}