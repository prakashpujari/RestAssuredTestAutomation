package com.company.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.response.Response;

public class ClientHeaderFilter implements Filter {

    private final String clientId;
    private final String clientSecret;

    public ClientHeaderFilter(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        requestSpec.header("client_id", clientId);
        requestSpec.header("client_secret", clientSecret);

        return ctx.next(requestSpec, responseSpec);
    }
}
