package com.company.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.util.UUID;

/**
 * Filter to generate and inject a unique correlation ID for each request.
 *
 * MuleSoft 4.9 compatibility:
 * - Correlation ID is essential for request tracing across MuleSoft components
 * - Helps in debugging and log correlation
 * - Can be disabled via system property for testing
 */
public class CorrelationIdFilter implements Filter {

    private final boolean enabled;

    public CorrelationIdFilter() {
        this.enabled = Boolean.parseBoolean(System.getProperty("correlation.id.enabled", "true"));
    }

    public CorrelationIdFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        if (enabled) {
            // Check if correlation_id is already set, if not generate one
            if (requestSpec.getHeaders().get("correlation_id") == null) {
                String correlationId = UUID.randomUUID().toString();
                requestSpec.header("correlation_id", correlationId);
            }
        }

        // Proceed with the request
        return ctx.next(requestSpec, responseSpec);
    }
}