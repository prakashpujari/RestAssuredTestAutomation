package com.company.api.core;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for all API tests.
 * Initializes WireMock and RestAssured configuration before each test.
 */
public class BaseApiTest {

    private WireMockServer wireMockServer;
    private static final int WIREMOCK_PORT = 8089;

    /**
     * Sets up WireMock server and RestAssured configuration before each test.
     */
    @BeforeEach
    void setUpWireMock() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        // Configure WireMock to localhost
        WireMock.configureFor("localhost", WIREMOCK_PORT);

        // Set the environment URL so that EnvironmentResolver picks it up
        String wireMockUrl = "http://localhost:" + WIREMOCK_PORT;
        System.setProperty("env.url", wireMockUrl);

        // Let ApiConfig set up common configuration (filters, timeout, etc.)
        ApiConfig.configure();

        // Override baseURI and port as per task requirements
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = WIREMOCK_PORT;

        // Call setupStubs() to configure endpoints
        setupStubs();
    }

    /**
     * Stops WireMock server after each test.
     */
    @AfterEach
    void tearDownWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
        // Clean up system property and RestAssured configuration
        System.clearProperty("env.url");
        RestAssured.reset();
    }

    /**
     * Sets up WireMock stubs for the API endpoints.
     * To be overridden by subclasses to provide specific stubs.
     */
    protected void setupStubs() {
        // Default implementation does nothing.
        // Subclasses should override this method to provide specific stubs.
    }
}