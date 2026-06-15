# 🚀 RestAssured API Test Automation Framework

Enterprise-grade API testing framework for MuleSoft APIs using RestAssured with support for MuleSoft 4.6 → 4.9 upgrade validation.

## ✨ Features

- **WireMock Integration** - Mock API endpoints for testing
- **Multiple Authentication** - Basic Auth, OAuth2, API Key, No Auth
- **Excel Export** - Test results exported to Excel format
- **UI Dashboard** - Web-based interface for API testing
- **MuleSoft 4.9 Ready** - Validated for MuleSoft 4.6 → 4.9 upgrade

## 📦 Project Structure

```
src/
├── main/
│   ├── java/com/company/api/
│   │   ├── Application.java
│   │   ├── config/              # Configuration classes
│   │   ├── controller/          # REST controllers
│   │   ├── core/              # Core test components
│   │   ├── model/             # Data models
│   │   └── service/           # Business services
│   └── resources/
│       ├── static/
│       │   └── dashboard.html   # Web UI
│       └── application.properties
└── test/
    ├── java/com/company/api/
    │   ├── builders/            # Test data builders
    │   ├── core/                # Base test classes
    │   ├── generator/           # Automated test generator
    │   ├── models/              # Test models
    │   └── tests/               # Test classes
    └── resources/
        └── config/apis.yaml     # API definitions
```

## 🚀 Quick Start

### Run Tests
```bash
mvn test
```

### Run Specific Test
```bash
mvn test -Dtest=CustomerApiTestMuleSoft49
```

### Start UI Dashboard
```bash
mvn spring-boot:run
# Open http://localhost:8080/dashboard.html
```

## 🧪 Test Categories

| Category | Tests | Description |
|----------|-------|-------------|
| Positive Tests | 5 | Happy path scenarios |
| Negative Tests | 6 | 400/404 error handling |
| Policy Tests | 5 | Authentication |
| Rate Limiting | 1 | 429 responses |
| CORS Tests | 1 | Preflight handling |
| Server Errors | 2 | 500/503 responses |
| Retry Logic | 1 | Retry-After header |
| Validation | 2 | Schema validation |

## 🖥️ Using the UI Dashboard

### Step 1: Start Application
```bash
mvn spring-boot:run
```

### Step 2: Open Dashboard
Navigate to: **http://localhost:8080/dashboard.html**

### Step 3: Configure API
1. **Endpoint URL**: `https://api.example.com/v1/resource`
2. **HTTP Method**: GET/POST/PUT/DELETE
3. **Authentication**: No Auth / Basic Auth / API Key
4. **Client ID/Secret**: For authenticated APIs
5. **Request Body**: JSON for POST/PUT

### Step 4: Run Tests
Click **▶ Generate & Run Tests**

### Step 5: View Results
- Status: PASS/FAIL
- HTTP Status Code
- Response Duration
- Response Data

## ✨ Features

- **Environment-Driven Configuration**: Maven profiles for local/dev/qa/uat environments
- **Correlation ID Tracking**: Automatic UUID generation for request tracing
- **Comprehensive Logging**: Detailed request/response logging with headers and bodies
- **Retry Logic**: Configurable retry attempts for flaky endpoints
- **Modular Architecture**: Separation of concerns (core, builders, models, tests)
- **Builder Pattern**: Fluent interface for test data creation
- **JSON Schema Validation**: Optional schema-based response validation
- **Allure Reporting**: Optional rich HTML test reports
- **WireMock Support**: Isolated testing without external dependencies
- **JUnit 5 Integration**: Modern testing with nested classes and display names
- **Policy Testing**: Client ID enforcement, CORS, rate limiting, OAuth validation
- **CI/CD Ready**: Maven profiles, GitHub Actions, Jenkins pipeline examples

## 🏗️ Project Structure

```
RestAssuredTestAutomation/
├── pom.xml                     # Maven configuration with dependencies & profiles
├── USAGE_GUIDE.md             # Comprehensive usage instructions
├── SAMPLE_TEST_EXECUTION.md   # Expected test output and screenshot descriptions
├── README.md                  # This file
└── src/
    └── test/
        ├── java/
        │   └── com/
        │       └── company/
        │           └── api/
        │               ├── core/              # Framework components
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
            └── schemas/                       # JSON schemas
                └── customer-schema.json
```

## 🚀 Getting Started

### Prerequisites
- Java JDK 17 or higher
- Apache Maven 3.6 or higher
- Git (for cloning)
- 2GB+ RAM recommended

### Installation
```bash
# Clone the repository
git clone <repository-url>
cd RestAssuredTestAutomation

# Install dependencies
mvn clean install -DskipTests
```

## 📖 Usage Guide

See [USAGE_GUIDE.md](USAGE_GUIDE.md) for comprehensive instructions on:
- Running tests for different environments
- Extending the framework with new APIs
- Configuring system properties
- Understanding core components
- Best practices for test design

### Quick Start Commands
```bash
# Run all tests
mvn test

# Run tests for specific environment
mvn test -Pdev     # Development
mvn test -Pqa      # QA
mvn test -Put      # UAT

# Run only WireMock tests (no external API needed)
mvn test -Dtest=CustomerApiTestWithWireMock

# Generate Allure reports
mvn allure:serve
```

## 📊 Sample Test Execution

See [SAMPLE_TEST_EXECUTION.md](SAMPLE_TEST_EXECUTION.md) for:
- Expected console output from test execution
- Descriptions of what screenshots would show
- Detailed walkthrough of test execution flow
- Performance characteristics and resource usage

## 🔧 Extending the Framework

The framework is designed for easy extension:

### Adding a New API
1. Create data model in `src/test/java/com/company/api/models/`
2. Create request builder in `src/test/java/com/company/api/builders/`
3. Create test suite in `src/test/java/com/company/api/tests/<api-name>/`
4. Add JSON schema to `src/test/resources/schemas/` (optional)
5. Use the pattern from existing Customer API tests

### Adding Custom Features
- **New filters**: Implement `io.restassured.filter.Filter` and add to `ApiConfig.configure()`
- **New environments**: Add Maven profile in `pom.xml` or extend `EnvironmentResolver.java`
- **New assertions**: Use Hamcrest matchers or create custom matchers
- **New report formats**: Configure additional Allure or Maven plugins

## 🔄 CI/CD Integration

See [USAGE_GUIDE.md#cicd-integration](USAGE_GUIDE.md#cicd-integration) for:
- Jenkins pipeline examples
- GitHub Actions workflow (`.github/workflows/api-tests.yml`)
- Docker-based CI examples
- Maven profile usage in automated pipelines

### Typical CI Pipeline
1. Checkout code
2. Set up JDK 17
3. Cache Maven dependencies
4. Run tests: `mvn verify -P<environment>`
5. Generate reports: `mvn allure:report`
6. Publish test results and reports as artifacts

## 🐛 Troubleshooting

See [USAGE_GUIDE.md#troubleshooting](USAGE_GUIDE.md#troubleshooting) for solutions to:
- Memory issues and JVM tuning
- Port conflicts with WireMock
- Compilation errors related to Java versions
- Dependency conflicts
- Test flakiness and retry configuration
- Environment configuration problems

### Common Fixes
```bash
# Increase memory for Maven
export MAVEN_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g"

# Force Maven to update dependencies
mvn clean install -U

# Skip tests for compilation only
mvn compile -DskipTests

# Run tests with debugging
mvn test -Dmaven.surefire.debug
```

## 🛡️ Design Principles

1. **Separation of Concerns**: Configuration, logging, retries, and correlation are decoupled
2. **Convention over Configuration**: Sensible defaults with easy overrides
3. **Fail Fast**: Clear error messages and validation
4. **Observability**: Comprehensive logging and tracing capabilities
5. **Extensibility**: Plug-and-play architecture for new features
6. **Reliability**: Built-in retry mechanisms for transient failures
7. **Testability**: Isolated testing with WireMock support
8. **Maintainability**: Clear naming, documentation, and consistent patterns

## 📝 License

This framework is provided for educational and internal use purposes. Please refer to your organization's licensing policies for usage in production environments.

---

**Ready to test your MuleSoft APIs?** Start with the [Usage Guide](USAGE_GUIDE.md) and run your first test with `mvn test -Dtest=CustomerApiTestWithWireMock`!