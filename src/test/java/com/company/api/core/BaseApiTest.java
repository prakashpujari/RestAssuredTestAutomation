package com.company.api.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

/**
 * Base class for all API tests.
 * Initializes RestAssured configuration before each test.
 */
public class BaseApiTest {

    @BeforeEach
    public void setUp() {
        ApiConfig.configure();
    }

    @AfterEach
    public void tearDown() {
        ApiConfig.reset();
    }
}