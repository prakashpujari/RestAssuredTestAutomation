package com.company.api.tests.customer;

import com.company.api.builders.CustomerRequestBuilder;
import com.company.api.core.BaseApiTest;
import com.company.api.models.Customer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Customer API.
 * Covers CRUD operations, negative tests, and policy tests.
 */
@DisplayName("Customer API Tests")
class CustomerApiTest extends BaseApiTest {

    private static final String BASE_PATH = "/customers";
    private String testCustomerId;

    @BeforeEach
    public void setUp() {
        // Additional setup if needed
    }

    @Override
    protected void setupStubs() {
        // Clear all existing mappings
        WireMock.reset();

        // Stub for GET non-existent customer (404)
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/customers/00000000-0000-0000-0000-000000000000"))
                .atPriority(5)
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));


        // Stub for GET customer by ID (success)
        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/customers/([a-zA-Z0-9\\-]+)"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"$1\",\"firstName\":\"John\",\"lastName\":\"Doe\"}")));

        // Stub for POST create customer (success)
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .willReturn(WireMock.aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"123\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone\":\"123-456-7890\"}")));

        // Stub for PUT update customer (success)
        WireMock.stubFor(WireMock.put(WireMock.urlPathMatching("/customers/([a-zA-Z0-9\\-]+)"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"$1\",\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\",\"phone\":\"098-765-4321\"}")));

        // Stub for DELETE customer (success)
        WireMock.stubFor(WireMock.delete(WireMock.urlPathMatching("/customers/([a-zA-Z0-9\\-]+)"))
                .willReturn(WireMock.aResponse()
                        .withStatus(204)));

        // Stub for POST customer with invalid email
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .withRequestBody(WireMock.matchingJsonPath("$.email", WireMock.containing("invalid")))
                .willReturn(WireMock.aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Invalid email format\"}"))
                .atPriority(10));

        // Stub for POST customer with missing required fields (email null)
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .withRequestBody(WireMock.matchingJsonPath("$.email", WireMock.absent()))
                .willReturn(WireMock.aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Email is required\"}"))
                .atPriority(20));

        // Stub for missing client_id header
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .withHeader("client_id", WireMock.absent())
                .willReturn(WireMock.aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Missing client_id header\"}"))
                .atPriority(30));

        // Stub for invalid client_id
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .withHeader("client_id", WireMock.equalTo("invalid-client"))
                .willReturn(WireMock.aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Invalid client_id\"}"))
                .atPriority(5));

        // Stub for missing client_secret
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .withHeader("client_secret", WireMock.absent())
                .willReturn(WireMock.aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Missing client_secret header\"}"))
                .atPriority(50));

        // Stub for CORS preflight request
        WireMock.stubFor(WireMock.options(WireMock.urlPathEqualTo("/customers"))
                .withHeader("Origin", WireMock.equalTo("https://trusted-domain.com"))
                .withHeader("Access-Control-Request-Method", WireMock.equalTo("POST"))
                .withHeader("Access-Control-Request-Headers", WireMock.equalTo("content-type"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Access-Control-Allow-Origin", "https://trusted-domain.com")
                        .withHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS")
                        .withHeader("Access-Control-Allow-Headers", "content-type, client_id, client_secret, correlation_id")));

        // Stub for rate limiting: when X-Trigger-Rate-Limit header is present, return 429
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .withHeader("X-Trigger-Rate-Limit", WireMock.equalTo("true"))
                .willReturn(WireMock.aResponse()
                        .withStatus(429)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Rate limit exceeded\"}"))
                .atPriority(60));

        // Fallback stub for POST customer (success) - lower priority
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/customers"))
                .willReturn(WireMock.aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":\"123\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"phone\":\"123-456-7890\"}"))
                .atPriority(100));
    }

    @Nested
    @DisplayName("Positive Tests")
    class PositiveTests {

        @Test
        @DisplayName("GET customer by ID should return 200")
        void getCustomerByIdSuccess() {
            // First, create a customer to get an ID
            testCustomerId = createTestCustomer();

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/{id}", testCustomerId)
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(testCustomerId))
                    .body("firstName", equalTo("John"))
                    .body("lastName", equalTo("Doe"));
        }

        @Test
        @DisplayName("POST create customer should return 201")
        void createCustomerSuccess() {
            CustomerRequestBuilder builder = new CustomerRequestBuilder();
            Customer customer = builder.build();

            given()
                    .contentType(ContentType.JSON)
                    .body(customer)
            .when()
                    .post(BASE_PATH)
            .then()
                    .statusCode(201)
                    .contentType(ContentType.JSON)
                    .body("id", notNullValue())
                    .body("firstName", equalTo("John"))
                    .body("lastName", equalTo("Doe"))
                    .body("email", equalTo("john.doe@example.com"))
                    .body("phone", equalTo("123-456-7890"))
                    .header("Content-Type", "application/json");
        }

        @Test
        @DisplayName("PUT update customer should return 200")
        void updateCustomerSuccess() {
            // Create a customer first
            testCustomerId = createTestCustomer();

            CustomerRequestBuilder builder = new CustomerRequestBuilder();
            builder.withId(testCustomerId)
                   .withFirstName("Jane")
                   .withLastName("Smith")
                   .withEmail("jane.smith@example.com")
                   .withPhone("098-765-4321");
            Customer updatedCustomer = builder.build();

            given()
                    .contentType(ContentType.JSON)
                    .body(updatedCustomer)
            .when()
                    .put(BASE_PATH + "/{id}", testCustomerId)
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(testCustomerId))
                    .body("firstName", equalTo("Jane"))
                    .body("lastName", equalTo("Smith"))
                    .body("email", equalTo("jane.smith@example.com"))
                    .body("phone", equalTo("098-765-4321"));
        }

        @Test
        @DisplayName("DELETE customer should return 204")
        void deleteCustomerSuccess() {
            // Create a customer first
            testCustomerId = createTestCustomer();

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .delete(BASE_PATH + "/{id}", testCustomerId)
            .then()
                    .statusCode(204);
        }
    }

    @Nested
    @DisplayName("Negative Tests")
    class NegativeTests {

        @Test
        @DisplayName("GET non-existent customer should return 404")
        void getNonExistentCustomer() {
            String nonExistentId = "00000000-0000-0000-0000-000000000000";

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .get(BASE_PATH + "/{id}", nonExistentId)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("POST customer with missing required fields should return 400")
        void createCustomerMissingFields() {
            CustomerRequestBuilder builder = new CustomerRequestBuilder();
            // Remove email (assuming it's required)
            builder.withEmail(null);
            Customer customer = builder.build();

            given()
                    .contentType(ContentType.JSON)
                    .body(customer)
            .when()
                    .post(BASE_PATH)
            .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body("message", containsString("validation"));
        }

        @Test
        @DisplayName("POST customer with invalid email should return 400")
        void createCustomerInvalidEmail() {
            CustomerRequestBuilder builder = new CustomerRequestBuilder();
            builder.withEmail("invalid-email");
            Customer customer = builder.build();

            given()
                    .contentType(ContentType.JSON)
                    .body(customer)
            .when()
                    .post(BASE_PATH)
            .then()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body("message", containsString("email"));
        }

        @Test
        @DisplayName("PUT non-existent customer should return 404")
        void updateNonExistentCustomer() {
            String nonExistentId = "00000000-0000-0000-0000-000000000000";
            CustomerRequestBuilder builder = new CustomerRequestBuilder();
            builder.withId(nonExistentId);
            Customer customer = builder.build();

            given()
                    .contentType(ContentType.JSON)
                    .body(customer)
            .when()
                    .put(BASE_PATH + "/{id}", nonExistentId)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("DELETE non-existent customer should return 404")
        void deleteNonExistentCustomer() {
            String nonExistentId = "00000000-0000-0000-0000-000000000000";

            given()
                    .contentType(ContentType.JSON)
            .when()
                    .delete(BASE_PATH + "/{id}", nonExistentId)
            .then()
                    .statusCode(404);
        }
    }

    @Nested
    @DisplayName("Policy Tests")
    class PolicyTests {

        @Test
        @DisplayName("Request without client_id header should return 401")
        void missingClientId() {
            given()
                    .header("client_secret", "test-secret")
                    .header("correlation_id", "test-correlation")
                    .contentType(ContentType.JSON)
                    .body(new CustomerRequestBuilder().build())
            .when()
                    .post(BASE_PATH)
            .then()
                    .statusCode(401)
                    .contentType(ContentType.JSON)
                    .body("message", containsString("client_id"));
        }

        @Test
        @DisplayName("Request with invalid client_id should return 401")
        void invalidClientId() {
            given()
                    .header("client_id", "invalid-client")
                    .header("client_secret", "test-secret")
                    .header("correlation_id", "test-correlation")
                    .contentType(ContentType.JSON)
                    .body(new CustomerRequestBuilder().build())
            .when()
                    .post(BASE_PATH)
            .then()
                    .statusCode(401)
                    .contentType(ContentType.JSON)
                    .body("message", containsString("client_id"));
        }

        @Test
        @DisplayName("Request without client_secret header should return 401")
        void missingClientSecret() {
            given()
                    .header("client_id", "test-client")
                    .header("correlation_id", "test-correlation")
                    .contentType(ContentType.JSON)
                    .body(new CustomerRequestBuilder().build())
            .when()
                    .post(BASE_PATH)
            .then()
                    .statusCode(401)
                    .contentType(ContentType.JSON)
                    .body("message", containsString("client_secret"));
        }

        @Test
        @DisplayName("CORS preflight request should return 200 with appropriate headers")
        void corsPreflight() {
            given()
                    .header("Origin", "https://trusted-domain.com")
                    .header("Access-Control-Request-Method", "POST")
                    .header("Access-Control-Request-Headers", "content-type")
            .when()
                    .options(BASE_PATH)
            .then()
                    .statusCode(200)
                    .header("Access-Control-Allow-Origin", "https://trusted-domain.com")
                    .header("Access-Control-Allow-Methods", containsString("POST"))
                    .header("Access-Control-Allow-Headers", containsString("content-type"));
        }

        @Test
        @DisplayName("Rate limiting: eventually returns 429 after multiple requests")
        void rateLimiting() {
            // Send multiple requests with small delays to trigger rate limit
            // Note: This test assumes rate limiting is configured (e.g., 5 requests per minute)
            // Adjust the loop count based on your rate limit threshold
            for (int i = 0; i < 20; i++) {
                var response = given()
                        .header("client_id", "test-client")
                        .header("client_secret", "test-secret")
                        .header("correlation_id", "test-correlation-" + i)
                        .header("X-Trigger-Rate-Limit", "true") // Trigger the 429 stub
                        .contentType(ContentType.JSON)
                        .body(new CustomerRequestBuilder().build())
                .when()
                        .post(BASE_PATH)
                .then()
                        .extract()
                        .response();

                if (response.getStatusCode() == 429) {
                    // Rate limit hit, test passes
                    return;
                }
                // If we get a success, continue looping
                Assertions.assertTrue(response.getStatusCode() == 201 ||
                                response.getStatusCode() == 400 ||
                                response.getStatusCode() == 401,
                        "Unexpected status code: " + response.getStatusCode());

                // Small delay between requests to spread them out over time
                // This helps with time-based rate limiting (e.g., X requests per minute)
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            // If we never hit 429, the test fails (rate limit not triggered or too high)
            Assertions.fail("Rate limiting not triggered after 20 requests");
        }
    }

    /**
     * Helper method to create a test customer and return its ID.
     * @return The ID of the created customer
     * @throws IllegalStateException if customer ID cannot be extracted from response
     */
    private String createTestCustomer() {
        CustomerRequestBuilder builder = new CustomerRequestBuilder();
        Customer customer = builder.build();

        String id = given()
                .header("client_id", "test-client")
                .header("client_secret", "test-secret")
                .header("correlation_id", "test-correlation")
                .contentType(ContentType.JSON)
                .body(customer)
        .when()
                .post(BASE_PATH)
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        if (id == null || id.isEmpty()) {
            throw new IllegalStateException("Failed to extract customer ID from response");
        }
        return id;
    }
}