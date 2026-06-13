package com.company.api.builders;

import com.company.api.models.Customer;
import com.company.api.builders.TestDataConstants;

/**
 * Builder for creating Customer objects for API requests.
 * Uses the Builder pattern for fluent interface.
 */
public class CustomerRequestBuilder {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    public CustomerRequestBuilder() {
        // Initialize with default test data
        this.firstName = TestDataConstants.DEFAULT_FIRST_NAME;
        this.lastName = TestDataConstants.DEFAULT_LAST_NAME;
        this.email = TestDataConstants.DEFAULT_EMAIL;
        this.phone = TestDataConstants.DEFAULT_PHONE;
    }

    public CustomerRequestBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public CustomerRequestBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public CustomerRequestBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public CustomerRequestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public CustomerRequestBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Customer build() {
        return new Customer(id, firstName, lastName, email, phone);
    }
}