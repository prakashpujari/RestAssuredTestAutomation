package com.company.api.core;
import com.company.api.core.RetryFilter;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import com.company.api.core.EnvironmentResolver;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;

import java.util.Arrays;
import java.util.List;

public class ApiConfig {

    public static void configure() {

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
        List<Filter> filters = Arrays.asList(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new RetryFilter(3, 1000)   // <-- YOUR CUSTOM RETRY FILTER
        );

        RestAssured.filters(filters);
    }

    public static void reset() {
        RestAssured.reset();
    }
}
