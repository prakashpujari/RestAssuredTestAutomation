package com.company.api.core;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import com.company.api.core.EnvironmentResolver;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Central configuration class for RestAssured test framework.
 * Configures base URL, timeout, logging, and filters for all API tests.
 *
 * MuleSoft 4.9 compatibility notes:
 * - ClientHeaderFilter: Required for client_id/client_secret authentication
 * - Correlation ID: Added automatically for request tracing
 * - RetryFilter: Handles transient failures common in MuleSoft runtime
 */
public class ApiConfig {

    // Default client credentials for testing
    public static final String DEFAULT_CLIENT_ID = System.getProperty("test.client.id", "test-client");
    public static final String DEFAULT_CLIENT_SECRET = System.getProperty("test.client.secret", "test-secret");

    public static void configure() {
        configure(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET);
    }

    /**
     * Configures RestAssured with the specified client credentials.
     * @param clientId Client ID for authentication
     * @param clientSecret Client secret for authentication
     */
    public static void configure(String clientId, String clientSecret) {
        // Base URL
        String baseUrl = EnvironmentResolver.getBaseUrl();
        RestAssured.baseURI = baseUrl;
        System.out.println("ApiConfig.configure(): Setting RestAssured baseURI to: " + baseUrl);

        // Timeout
        int timeoutMillis = Integer.parseInt(System.getProperty("restassured.timeout", "10000"));
        RestAssured.config = RestAssuredConfig.newConfig()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMillis)
                        .setParam("http.socket.timeout", timeoutMillis));

        // Logging
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Filters (CORRECT WAY)
        // Order matters: ClientHeaderFilter first, then CorrelationIdFilter, then logging
        List<Filter> filters = Arrays.asList(
                new ClientHeaderFilter(clientId, clientSecret),  // MuleSoft auth headers (FIRST)
                new CorrelationIdFilter(),                        // Correlation ID for tracing
                new RetryFilter(3, 1000),                        // Retry on transient failures
                new RequestLoggingFilter(),                      // Log requests
                new ResponseLoggingFilter()                      // Log responses
        );

        RestAssured.filters(filters);
    }

    /**
     * Resets RestAssured configuration to defaults.
     */
    public static void reset() {
        RestAssured.reset();
    }
}
