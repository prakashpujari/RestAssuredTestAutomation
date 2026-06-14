package com.company.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class RetryFilter implements Filter {

    private final int maxRetries;
    private final int delayMillis;

    public RetryFilter(int maxRetries, int delayMillis) {
        this.maxRetries = maxRetries;
        this.delayMillis = delayMillis;
    }

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification response,
                           FilterContext ctx) {

        int attempts = 0;

        while (true) {
            try {
                return ctx.next(request, response);
            } catch (Exception ex) {
                attempts++;
                if (attempts > maxRetries) {
                    throw ex;
                }
                if (delayMillis > 0) {
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
    }
}
