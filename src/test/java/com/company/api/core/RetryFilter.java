package com.company.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.lang.InterruptedException;
import java.lang.Thread;

/**
 * Filter to retry failed requests (e.g., due to network flakiness).
 */
public class RetryFilter implements Filter {

    private final int maxRetries;

    public RetryFilter(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        int attempts = 0;
        Response response = null;

        while (attempts < maxRetries) {
            try {
                response = ctx.next(requestSpec, responseSpec);
                // If status code is less than 500 (i.e., not server error), consider it successful
                // Only retry on 5xx server errors, not on 4xx client errors
                if (response.getStatusCode() < 500) {
                    break;
                }
                // If we get here, we have a 5xx error, so retry
            } catch (Exception e) {
                // On exception, retry
            }
            attempts++;
            if (attempts < maxRetries) {
                System.out.println("Request failed (attempt " + (attempts + 1) + "). Retrying...");
                try {
                    // Wait before retry with exponential backoff: 1s, 2s, 4s, 8s, ...
                    Thread.sleep(1000L * (long) Math.pow(2, attempts - 1));
                } catch (InterruptedException ignored) {
                }
            }
        }

        // If we haven't succeeded after maxRetries attempts, make one final attempt
        if (response == null) {
            try {
                response = ctx.next(requestSpec, responseSpec);
            } catch (Exception e) {
                throw new IllegalStateException("Request failed after " + maxRetries + " retries", e);
            }
        }
        return response;
    }
}