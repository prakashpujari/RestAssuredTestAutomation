package com.company.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.Map;

/**
 * Filter to log request and response details.
 */
public class LoggingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        // Log request details
        System.out.println("=== API REQUEST ===");
        System.out.println("Method: " + requestSpec.getMethod());
        System.out.println("URI: " + requestSpec.getURI());
        System.out.println("Headers: " + requestSpec.getHeaders());
        System.out.println("Query Parameters: " + requestSpec.getQueryParams());
        System.out.println("Body: " + requestSpec.getBody());
        // Get correlation ID from headers since we set it there
        @SuppressWarnings("unchecked")
        Map<String, String> headers = (Map<String, String>) requestSpec.getHeaders();
        String correlationId = headers.get("correlation_id");
        if (correlationId != null) {
            System.out.println("Correlation ID: " + correlationId);
        }

        // Execute request
        Response response = ctx.next(requestSpec, responseSpec);

        // Log response details
        System.out.println("=== API RESPONSE ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Body: " + response.getBody().asString());
        System.out.println("===================");

        return response;
    }
}