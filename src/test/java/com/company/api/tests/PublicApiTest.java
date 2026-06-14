package com.company.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * End-to-end tests for truly free public APIs (no auth required).
 * Tests JSONPlaceholder and HTTPBin - both are free and reliable.
 * Run with: mvn test -Dtest=PublicApiTest
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PublicApiTest {

    @BeforeAll
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    @DisplayName("JSONPlaceholder API Tests")
    class JsonPlaceholderTests {

        @Test
        @DisplayName("GET /posts - List all posts")
        void listPosts() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
            .when()
                    .get("/posts")
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", greaterThan(0))
                    .body("[0].userId", notNullValue())
                    .body("[0].id", notNullValue())
                    .body("[0].title", notNullValue());
        }

        @Test
        @DisplayName("GET /posts/1 - Get single post")
        void getPost() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
                    .pathParam("id", 1)
            .when()
                    .get("/posts/{id}")
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(1))
                    .body("userId", notNullValue())
                    .body("title", notNullValue())
                    .body("body", notNullValue());
        }

        @Test
        @DisplayName("GET /posts/99999 - Post not found (404)")
        void getPostNotFound() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
                    .pathParam("id", 99999)
            .when()
                    .get("/posts/{id}")
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("POST /posts - Create post")
        void createPost() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"title\": \"foo\", \"body\": \"bar\", \"userId\": 1}")
            .when()
                    .post("/posts")
            .then()
                    .statusCode(201)
                    .contentType(ContentType.JSON)
                    .body("id", notNullValue())
                    .body("title", equalTo("foo"));
        }

        @Test
        @DisplayName("GET /posts?userId=1 - Filter posts")
        void filterPostsByUser() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
                    .queryParam("userId", 1)
            .when()
                    .get("/posts")
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("userId", everyItem(equalTo(1)));
        }
    }

    @Nested
    @DisplayName("JSONPlaceholder Additional Resources")
    class JsonPlaceholderAdditionalTests {

        @Test
        @DisplayName("GET /comments - List comments")
        void listComments() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
            .when()
                    .get("/comments")
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", greaterThan(0));
        }

        @Test
        @DisplayName("GET /albums - List albums")
        void listAlbums() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
            .when()
                    .get("/albums")
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", greaterThan(0));
        }

        @Test
        @DisplayName("GET /todos - List todos")
        void listTodos() {
            RestAssured.baseURI = "https://jsonplaceholder.typicode.com";

            given()
            .when()
                    .get("/todos")
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", greaterThan(0))
                    .body("[0].completed", notNullValue());
        }
    }

    @Nested
    @DisplayName("HTTPBin API Tests")
    class HttpBinTests {

        @Test
        @DisplayName("GET /get - Echo request")
        void getEcho() {
            RestAssured.baseURI = "https://httpbin.org";

            given()
                    .queryParam("test", "value")
            .when()
                    .get("/get")
            .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("args.test", equalTo("value"));
        }

        @Test
        @DisplayName("GET /status/200 - Return status code")
        void getStatus200() {
            RestAssured.baseURI = "https://httpbin.org";

            given()
            .when()
                    .get("/status/200")
            .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("GET /status/404 - Return 404 status")
        void getStatus404() {
            RestAssured.baseURI = "https://httpbin.org";

            given()
            .when()
                    .get("/status/404")
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("POST /post - Echo POST body")
        void postEcho() {
            RestAssured.baseURI = "https://httpbin.org";

            given()
                    .contentType(ContentType.JSON)
                    .body("{\"name\": \"test\"}")
            .when()
                    .post("/post")
            .then()
                    .statusCode(200)
                    .body("json.name", equalTo("test"));
        }

        @Test
        @DisplayName("GET /headers - Check headers")
        void checkHeaders() {
            RestAssured.baseURI = "https://httpbin.org";

            given()
                    .header("X-Custom-Header", "custom-value")
            .when()
                    .get("/headers")
            .then()
                    .statusCode(200)
                    .body("headers.X-Custom-Header", equalTo("custom-value"));
        }

        @Test
        @DisplayName("GET /uuid - Get UUID")
        void getUuid() {
            RestAssured.baseURI = "https://httpbin.org";

            given()
            .when()
                    .get("/uuid")
            .then()
                    .statusCode(200)
                    .body("uuid", notNullValue());
        }
    }
}