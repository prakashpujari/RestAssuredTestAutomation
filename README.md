# Rest Assured API Testing Framework for MuleSoft APIs

An enterprise-grade API testing framework built with Java 17, Maven, JUnit 5, and Rest Assured 5.x for testing MuleSoft APIs. Features include environment-driven configuration, correlation ID tracking, comprehensive logging, retry logic, and CI/CD readiness.

## 📋 Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Usage Guide](#usage-guide)
- [Sample Test Execution](#sample-test-execution)
- [Extending the Framework](#extending-the-framework)
- [CI/CD Integration](#cicd-integration)
- [Troubleshooting](#troubleshooting)
- [License](#license)

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