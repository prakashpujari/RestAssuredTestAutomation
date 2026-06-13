# Rest Assured API Testing Framework - Usage Guide

This document provides comprehensive instructions on how to use the Rest Assured API Testing Framework for MuleSoft APIs.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Setup](#project-setup)
3. [Running Tests](#running-tests)
4. [Environment Configuration](#environment-configuration)
5. [Extending the Framework](#extending-the-framework)
6. [CI/CD Integration](#cicd-integration)
7. [Expected Test Results](#expected-test-results)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before using this framework, ensure you have:

- **Java Development Kit (JDK)**: Version 17 or higher
- **Apache Maven**: Version 3.6 or higher
- **Git**: For cloning the repository
- **IDE** (Optional): IntelliJ IDEA, Eclipse, or VS Code with Java support
- **Memory**: Minimum 2GB RAM recommended for smooth execution

### Verify Installation
```bash
# Check Java version
java -version
# Should show: openjdk version "17.0.x" or higher

# Check Maven version
mvn -v
# Should show Apache Maven 3.x.x
```

---

## Project Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd RestAssuredTestAutomation
```

### 2. Project Structure Overview
```
RestAssuredTestAutomation/
├── pom.xml                     # Maven configuration
├── USAGE_GUIDE.md             # This document
├── README.md                  # Project overview
└── src/
    └── test/
        ├── java/
        │   └── com/
        │       └── company/
        │           └── api/
        │               ├── core/              # Framework core components
        │               │   ├── ApiConfig.java
        │               │   ├── BaseApiTest.java
        │               │   ├── CorrelationIdFilter.java
        │               │   ├── EnvironmentResolver.java
        │               │   ├── LoggingFilter.java
        │               │   └── RetryFilter.java
        │               ├── builders/          # Request builders
        │               │   └── CustomerRequestBuilder.java
        │               ├── models/            # Data models
        │               │   └── Customer.java
        │               └── tests/             # API-specific test suites
        │                   └── customer/
        │                       ├── CustomerApiTest.java
        │                       └── CustomerApiTestWithWireMock.java
        └── resources/
            └── schemas/                       # JSON schemas for validation
                └── customer-schema.json
```

### 3. Install Dependencies
```bash
mvn clean install -DskipTests
```
This downloads all required dependencies defined in pom.xml.

---

## Running Tests

### 1. Run All Tests
```bash
mvn test
```

### 2. Run Tests for Specific Environment
The framework supports Maven profiles for different environments:

```bash
# Local environment (default)
mvn test -Plocal

# Development environment
mvn test -Pdev

# QA environment
mvn test -Pqa

# UAT environment
mvn test -Puat
```

### 3. Run Specific Test Classes
```bash
# Run only the WireMock-based tests (no external API needed)
mvn test -Dtest=CustomerApiTestWithWireMock

# Run tests against real API
mvn test -Dtest=CustomerApiTest
```

### 4. Run Specific Test Methods
```bash
mvn test -Dtest=CustomerApiTestWithWireMock#createCustomerSuccess
```

### 5. Skip Tests (for compilation only)
```bash
mvn clean compile -DskipTests
```

---

## Environment Configuration

### Maven Profiles (pom.xml)
The framework includes predefined profiles in pom.xml:

```xml
<profiles>
    <!-- Local Environment -->
    <profile>
        <id>local</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <env.url>http://localhost:8081</env.url>
            <env.name>local</env.name>
        </properties>
    </profile>

    <!-- Development Environment -->
    <profile>
        <id>dev</id>
        <properties>
            <env.url>https://dev-api.company.com/customers</env.url>
            <env.name>dev</env.name>
        </properties>
    </profile>
    
    <!-- QA and UAT profiles follow similar pattern -->
</profiles>
```

### System Properties Override
You can override environment settings using system properties:

```bash
# Override URL directly
mvn test -Denv.url=https://custom-api.example.com/customers

# Override environment name
mvn test -Denv=staging

# Both together
mvn test -Denv=staging -Denv.url=https://staging-api.example.com/customers
```

### How EnvironmentResolver Works
The `EnvironmentResolver.java` class:
1. Checks for `env.url` system property (highest priority)
2. If not set, checks `env` system property to determine environment
3. Falls back to default values based on environment name
4. Defaults to `local` environment if nothing is configured

---

## Core Framework Components

### 1. ApiConfig.java
Central configuration applied before each test:
- Sets base URL from EnvironmentResolver
- Adds filters: CorrelationIdFilter, LoggingFilter, RetryFilter
- Configures timeout (10 seconds)
- Enables automatic logging on validation failure
- Resets configuration after each test

### 2. BaseApiTest.java
JUnit 5 base class with:
- `@BeforeEach`: Calls ApiConfig.configure()
- `@AfterEach`: Calls ApiConfig.reset()
- All test classes should extend this

### 3. CorrelationIdFilter.java
Generates a unique UUID for each request and adds it as:
- Header: `correlation_id`
- Useful for tracing requests in distributed systems

### 4. LoggingFilter.java
Logs detailed request/response information:
- Method, URI, headers, query parameters, body
- Status code, response headers, response body
- Correlation ID from headers
- Output appears in console/test logs

### 5. RetryFilter.java
Implements retry logic for transient failures:
- Configurable number of retries (default: 3)
- Retries on HTTP 5xx errors or exceptions
- Waits between attempts (increasing delay)

---

## Extending the Framework

### Adding a New API Test Suite

#### Step 1: Create Data Model (if needed)
```java
// src/test/java/com/company/api/models/Order.java
package com.company.api.models;

public class Order {
    private String id;
    private String customerId;
    private double amount;
    // getters, setters, constructors
}
```

#### Step 2: Create Request Builder
```java
// src/test/java/com/company/api/builders/OrderRequestBuilder.java
package com.company.api.builders;

import com.company.api.models.Order;

public class OrderRequestBuilder {
    private String id;
    private String customerId;
    private double amount;

    public OrderRequestBuilder() {
        this.customerId = "CUST-001";
        this.amount = 100.0;
    }

    public OrderRequestBuilder withId(String id) { this.id = id; return this; }
    public OrderRequestBuilder withCustomerId(String customerId) { this.customerId = customerId; return this; }
    public OrderRequestBuilder withAmount(double amount) { this.amount = amount; return this; }

    public Order build() {
        return new Order(id, customerId, amount);
    }
}
```

#### Step 3: Create Test Suite
```java
// src/test/java/com/company/api/tests/order/OrderApiTest.java
package com.company.api.tests.order;

import com.company.api.builders.OrderRequestBuilder;
import com.company.api.core.BaseApiTest;
import com.company.api.models.Order;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class OrderApiTest extends BaseApiTest {
    private static final String BASE_PATH = "/orders";
    private String testOrderId;

    @Test
    void createOrderSuccess() {
        OrderRequestBuilder builder = new OrderRequestBuilder();
        Order order = builder.build();

        given()
                .contentType(ContentType.JSON)
                .body(order)
        .when()
                .post(BASE_PATH)
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("customerId", equalTo("CUST-001"))
                .body("amount", equalTo(100.0));
    }
    
    // Add more tests as needed...
}
```

#### Step 4: Add JSON Schema (Optional)
```json
// src/test/resources/schemas/order-schema.json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Order",
  "type": "object",
  "properties": {
    "id": { "type": "string" },
    "customerId": { "type": "string" },
    "amount": { "type": "number", "minimum": 0 }
  },
  "required": ["id", "customerId", "amount"]
}
```

#### Step 5: Use Schema in Tests
```java
@Test
void getOrderWithSchemaValidation() {
    // Create order first to get ID
    testOrderId = createTestOrder();

    given()
            .contentType(ContentType.JSON)
    .when()
            .get(BASE_PATH + "/{id}", testOrderId)
    .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(matchesJsonSchemaInClasspath("schemas/order-schema.json"));
}
```

### Adding Custom Filters
To add new functionality (e.g., OAuth token injection, custom headers):

1. Create a new filter implementing `io.restassured.filter.Filter`
2. Add it to the filter chain in `ApiConfig.configure()`
3. Example: OAuth token filter
```java
public class OAuthTokenFilter implements Filter {
    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        String token = getOAuthToken(); // Implement token retrieval
        requestSpec.header("Authorization", "Bearer " + token);
        return ctx.next(requestSpec, responseSpec);
    }
}
```

Then add in ApiConfig.configure():
```java
io.restassured.RestAssured.filters().add(new OAuthTokenFilter());
```

---

## CI/CD Integration

### Jenkins Pipeline Example
```groovy
pipeline {
    agent any
    environment {
        // Set environment variables for different stages
        TEST_ENV = 'qa'
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/company/rest-assured-test-automation.git'
            }
        }
        stage('Setup JDK') {
            steps {
                sh 'java -version'
            }
        }
        stage('Run API Tests') {
            steps {
                // Run tests with environment-specific configuration
                sh '''MAVEN_OPTS="-Xmx2g" mvn verify -P${TEST_ENV}'''
            }
        }
        stage('Generate Reports') {
            steps {
                sh 'mvn allure:report'
            }
        }
        stage('Publish Results') {
            steps {
                junit '**/target/surefire-reports/TEST-*.xml'
                // Publish Allure report if available
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
```

### GitHub Actions Workflow (`.github/workflows/api-tests.yml`)
```yaml
name: API Tests
on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        environment: [local, dev, qa]
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: 'maven'
    
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: |
          ${{ runner.os }}-m2
    
    - name: Run API Tests
      env:
        ENV: ${{ matrix.environment }}
      run: |
        MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m" mvn verify -P${{ matrix.environment }}
    
    - name: Generate Allure Report
      if: always()
      run: mvn allure:report
    
    - name: Publish Test Results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: test-results-${{ matrix.environment }}
        path: target/surefire-reports
    
    - name: Publish Allure Report
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: allure-report-${{ matrix.environment }}
        path: target/site/allure-maven-plugin
```

### Docker-Based CI Example
```dockerfile
# Dockerfile for CI
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn verify -Pdev -DskipITs

# For running tests in container
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/test-classes ./test-classes
COPY --from=builder /app/target/dependency ./dependency
CMD ["java", "-cp", "test-classes:dependency/*", "org.junit.platform.console.ConsoleLauncher", "--select-class", "com.company.api.tests.customer.CustomerApiTestWithWireMock"]
```

---

## Expected Test Results

When tests run successfully, you should see output similar to:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.company.api.tests.customer.CustomerApiTestWithWireMock
=== API REQUEST ===
Method: POST
URI: http://localhost:54321/customers
Headers: [correlation_id:"a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8", client_id:"test-client", client_secret:"test-secret", Content-Type:"application/json"]
Query Parameters: []
Body: Customer(id=null, firstName=John, lastName=Doe, email=john.doe@example.com, phone=123-456-7890)
Correlation ID: a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8
=== API RESPONSE ===
Status Code: 201
Headers: [Content-Type:"application/json", Server:"WireMock"]
Body: {"id":"123e4567-e89b-12d3-a456-426614174000","firstName":"John","lastName":"Doe","email":"john.doe@example.com","phone":"123-456-7890"}
===================
...
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.345 s - in com.company.api.tests.customer.CustomerApiTestWithWireMock
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.678 s
[INFO] Finished at: 2026-06-12T15:30:45-04:00
[INFO] ------------------------------------------------------------------------
```

### Allure Report Generation (Optional)
To generate beautiful HTML reports:
```bash
mvn allure:serve
```
This will:
1. Generate Allure report from test results
2. Open a web browser showing:
   - Test suite overview
   - Pass/fail statistics
   - Trend graphs
   - Detailed test execution steps
   - Attachments (request/response logs)
   - Environment information

### Test Categories Visible in Reports
Tests are organized using JUnit 5 `@Nested` and `@DisplayName`:
- **Positive Tests**: CRUD operations validation
- **Negative Tests**: Error handling validation
- **Policy Tests**: Security and governance policy validation

---

## Troubleshooting

### Common Issues and Solutions

#### 1. Memory Issues
**Symptom**: `java.lang.OutOfMemoryError` or native memory allocation failures
**Solution**:
```bash
# Increase heap size
export MAVEN_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g"
mvn test

# Or for GitHub Actions
env:
  MAVEN_OPTS: "-Xmx4g -XX:MaxMetaspaceSize=1g"
```

#### 2. Port Already in Use (WireMock)
**Symptom**: `Address already in use: bind` error
**Solution**: WireMock already uses dynamic ports, but if you see this:
- Kill existing Java processes: `taskkill /f /im java.exe` (Windows) or `pkill -f java` (Linux/Mac)
- Or specify a port range: Modify WireMockServer initialization

#### 3. Compilation Errors
**Symptom**: `error: invalid target release: 17`
**Solution**:
- Ensure JDK 17 is installed and JAVA_HOME is set correctly
- Check Maven compiler plugin configuration
- Verify `java.version` property in pom.xml

#### 4. Dependency Conflicts
**Symptom**: `ClassNotFoundException` or `NoSuchMethodError`
**Solution**:
- Run `mvn dependency:tree` to see dependency hierarchy
- Use `<exclusions>` in pom.xml if needed
- Ensure all Rest Assured modules use the same version

#### 5. Test Flakiness
**Symptom**: Tests pass intermittently
**Solution**:
- The RetryFilter already handles transient failures
- Increase retry count in ApiConfig.java if needed
- Ensure test data is properly isolated/cleaned up

#### 6. Environment Configuration Not Working
**Symptom**: Tests hitting wrong URL
**Solution**:
- Verify system properties are passed correctly
- Check that EnvironmentResolver.java logic is correct
- Use `-Denv.url` to explicitly set URL
- Add debug logging: `System.setProperty("env.url", "debug-url");`

### Debugging Tips

#### Enable Verbose Logging
```bash
mvn test -X
```

#### Run Single Test in Debug Mode
```bash
mvn test -Dtest=CustomerApiTestWithWireMock -Dmaven.surefire.debug
```
Then connect debugger to port 5005

#### View Generated Code
To see what WireMock stubs are registered:
```java
// Add to setUpWireMock() for debugging
System.out.println("Registered stubs:");
wireMockServer.findAll().forEach(System.out::println);
```

#### Check Actual Requests Made
The LoggingFilter already shows this, but you can also:
```java
// Add to test method
RequestLoggingLogger requestLogger = new RequestLoggingLogger();
given()
    .filter(requestLogger)
    .when()
    .post(BASE_PATH);
```

---

## Best Practices

### 1. Test Design
- **Arrange-Act-Assert**: Structure tests clearly
- **Independent Tests**: Each test should set up its own data
- **Descriptive Names**: Use `@DisplayName` for clear test purpose
- **Group Related Tests**: Use `@Nested` classes for organization

### 2. Data Management
- Use builders for test data creation
- Generate unique IDs (timestamps, random strings) when needed
- Clean up test data in `@AfterEach` if using real API
- Consider using testcontainers for database-backed APIs

### 3. Security
- Never hardcode credentials in test code
- Use environment variables or secret management for real credentials
- For CI/CD, use masked variables and protected environments

### 4. Performance
- Keep WireMock tests fast (< 1 second per test)
- Use connection pooling if making many real API calls
- Consider parallel test execution for large suites (`-Dparallel=methods`)

### 5. Maintenance
- Keep framework dependencies updated
- Document API-specific quirks in test comments
- Regularly review and remove obsolete tests
- Use tags/categories for test selection (`@Tag("smoke")`)

---

## Conclusion

This Rest Assured API Testing Framework provides a solid foundation for testing MuleSoft APIs with:

✅ **Enterprise-ready architecture** with separation of concerns  
✅ **Environment-driven configuration** for multi-stage deployments  
✅ **Comprehensive logging and tracing** via correlation IDs  
✅ **Resilience patterns** including retry logic  
✅ **Extensible design** for adding new APIs and features  
✅ **CI/CD integration** capabilities  
✅ **Isolated testing** with WireMock support  
✅ **Standard Java/Maven tooling** for wide team adoption  

To get started in a properly resourced environment:
1. Ensure adequate memory (2GB+ RAM recommended)
2. Run `mvn clean install -DskipTests` to download dependencies
3. Execute tests with `mvn test` or `mvn test -P<environment>`
4. View results in console or generate Allure reports for rich HTML output

The framework is ready to be extended for additional MuleSoft APIs, policies, and testing requirements as your organization's needs evolve.