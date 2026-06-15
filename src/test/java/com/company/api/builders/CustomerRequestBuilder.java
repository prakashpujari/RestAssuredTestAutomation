package com.company.api.builders;

import com.company.api.models.Customer;
import com.company.api.builders.TestDataConstants;

/**
 * Builder for creating Customer objects for API requests.
 * Uses the Builder pattern for fluent interface.
 *
 * MuleSoft 4.9 compatibility:
 * - Supports null field testing for validation
 * - Supports boundary value testing
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

    /**
     * Sets email to null for testing missing required fields.
     * @return this builder instance
     */
    public CustomerRequestBuilder withoutEmail() {
        this.email = null;
        return this;
    }

    /**
     * Creates an empty customer for testing missing fields.
     * @return Customer with all null fields
     */
    public CustomerRequestBuilder empty() {
        this.id = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phone = null;
        return this;
    }

    /**
     * Creates boundary test data - very long values.
     * @return this builder instance
     */
    public CustomerRequestBuilder withLongValues() {
        this.firstName = "A".repeat(255);
        this.lastName = "B".repeat(255);
        this.email = "a".repeat(245) + "@example.com";
        this.phone = "1".repeat(20);
        return this;
    }

    public Customer build() {
        return new Customer(id, firstName, lastName, email, phone);
    }
}