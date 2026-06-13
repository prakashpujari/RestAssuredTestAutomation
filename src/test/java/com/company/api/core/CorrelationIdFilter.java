package com.company.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.UUID;

/**
 * Filter to generate and inject a unique correlation ID for each request.
 */
public class CorrelationIdFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        // Generate a UUID for correlation ID
        String correlationId = UUID.randomUUID().toString();

        // Add correlation ID to headers
        requestSpec.header("correlation_id", correlationId);

        // Proceed with the request
        return ctx.next(requestSpec, responseSpec);
    }
}