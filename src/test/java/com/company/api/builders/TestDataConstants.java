package com.company.api.builders;

/**
 * Constants for test data used across the test automation framework.
 * Centralizing test data ensures consistency and makes it easier to update.
 */
public class TestDataConstants {

    // Default customer data
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    public static final String DEFAULT_EMAIL = "john.doe@example.com";
    public static final String DEFAULT_PHONE = "123-456-7890";

    // Default UUID for created customers in WireMock stubs
    public static final String DEFAULT_CUSTOMER_ID = "123e4567-e89b-12d3-a456-426614174000";

    // Updated customer data for PUT requests
    public static final String UPDATED_FIRST_NAME = "Jane";
    public static final String UPDATED_LAST_NAME = "Smith";
    public static final String UPDATED_EMAIL = "jane.smith@example.com";
    public static final String UPDATED_PHONE = "098-765-4321";

    // Private constructor to prevent instantiation
    private TestDataConstants() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}