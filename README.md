# 🚀 RestAssured API Test Automation Framework

Enterprise-grade API testing framework for MuleSoft APIs using RestAssured with support for MuleSoft 4.6 → 4.9 upgrade validation.

## 🏗️ Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            TEST EXECUTION FLOW                                 │
└─────────────────────────────────────────────────────────────────────────────────┘

┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│                  │     │                  │     │                  │
│   REST ASSURED   │────▶│    TEST CLASS    │────▶│   WIREMOCK       │
│    FRAMEWORK     │     │   (JUnit 5)      │     │   (Mock Server)  │
│                  │     │                  │     │                  │
└──────────────────┘     └──────────────────┘     └────────┬─────────┘
                                                             │
                                                             ▼
                                                    ┌──────────────────┐
                                                    │                  │
                                                    │   REAL API       │
                                                    │  (Target Mule)   │
                                                    │                  │
                                                    └──────────────────┘


┌─────────────────────────────────────────────────────────────────────────────────┐
│                          FRAMEWORK COMPONENTS                                   │
└─────────────────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────────────────┐
│  CORE LAYER (src/test/java/com/company/api/core/)                             │
├───────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐          │
│  │ BaseApiTest      │  │ ApiConfig        │  │ RetryFilter      │          │
│  │ - WireMock setup │  │ - Configures     │  │ - Retry logic    │          │
│  │ - Test lifecycle │  │   filters        │  │ - 3 retries      │          │
│  └──────────────────┘  │ - Timeout        │  └──────────────────┘          │
│                        │ - Logging        │                              │
│  ┌──────────────────┐  │ - Base URL       │  ┌──────────────────┐          │
│  │ CorrelationId    │  └──────────────────┘  │ ClientHeader     │          │
│  │ Filter           │                        │ Filter           │          │
│  │ - Adds trace ID  │  ┌──────────────────┐  │ - client_id      │          │
│  └──────────────────┘  │ Environment      │  │ - client_secret  │          │
│                        │ Resolver         │  └──────────────────┘          │
│                        │ - env.url prop   │                              │
│                        └──────────────────┘                              │
└───────────────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────────────────┐
│  BUILDER LAYER (src/test/java/com/company/api/builders/)                     │
├───────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐                                                       │
│  │ CustomerRequestBuilder                        │                          │
│  │ - Fluent API                                 │                          │
│  │ - withId(), withName(), withEmail()          │                          │
│  │ - withoutEmail(), empty(), withLongValues()  │                          │
│  └──────────────────┘                                                       │
└───────────────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────────────────┐
│  MODEL LAYER (src/test/java/com/company/api/models/)                          │
├───────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐                                                       │
│  │ Customer         │  (POJO for request/response)                          │
│  │ - id, name, email│                                                       │
│  │ - getters/setters│                                                       │
│  └──────────────────┘                                                       │
└───────────────────────────────────────────────────────────────────────────────┘

┌───────────────────────────────────────────────────────────────────────────────┐
│  TEST LAYER (src/test/java/com/company/api/tests/)                            │
├───────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐  ┌──────────────────┐                               │
│  │ CustomerApiTest  │  │ CustomerApiTest  │                               │
│  │ - Original       │  │ MuleSoft49       │                               │
│  │ - 14 tests       │  │ - 23 tests       │                               │
│  └──────────────────┘  └──────────────────┘                               │
│                                                                              │
│  ┌──────────────────┐  ┌──────────────────┐                               │
│  │ ApiTestGenerator │  │ ExcelExporter    │                               │
│  │ - Auto generates │  │ - Exports to     │                               │
│  │   test cases     │  │   Excel          │                               │
│  └──────────────────┘  └──────────────────┘                               │
└───────────────────────────────────────────────────────────────────────────────┘
```

## 🔄 Test Execution Flow

```
User Input
    │
    ▼
┌─────────────────┐
│   UI FORM       │
│ - Endpoint      │
│ - Method        │
│ - Auth Type     │
│ - Request Body  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ ApiTestGenerator│
│ - Builds config │
│ - Runs 3 tests  │
│   (valid,       │
│   invalid,      │
│   no-auth)      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   REST ASSURED  │
│ - Sends HTTP    │
│ - Captures      │
│   response      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ TestResult List │
│ - 3 results     │
│ - Status, Code, │
│   Duration      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ ExcelExporter   │
│ - Writes to     │
│   .xlsx file    │
└─────────────────┘
```

## 📊 Test Categories Breakdown

```
CustomerApiTestMuleSoft49 (23 tests)
├── Positive Tests (5)
│   ├── listCustomers
│   ├── getCustomerById
│   ├── createCustomer
│   ├── updateCustomer
│   └── deleteCustomer
│
├── Negative Tests (6)
│   ├── getNonExistentCustomer (404)
│   ├── createCustomerMissingEmail (400)
│   ├── createCustomerInvalidEmail (400)
│   ├── createCustomerEmptyBody (400)
│   ├── createCustomerInvalidJson (400)
│   └── createCustomerLongValues (boundary)
│
├── Policy Tests (5)
│   ├── missingClientId (401)
│   ├── invalidClientId (401)
│   ├── missingClientSecret (401)
│   ├── invalidClientSecret (403)
│   └── invalidCredentials (401)
│
├── Rate Limiting (1)
│   └── rateLimitingTriggered (429)
│
├── CORS Tests (1)
│   └── corsPreflight (200)
│
├── Server Errors (2)
│   ├── serverError (500)
│   └── serviceUnavailable (503)
│
├── Retry Logic (1)
│   └── retryAfterHeader
│
└── Validation (2)
    ├── responseSchemaValidation
    └── generatedIdValidation
```

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

## 🖥️ Using the UI Dashboard - Step by Step

### Step 1: Start the Application
```bash
mvn spring-boot:run
```
**Expected Output:**
```
Tomcat started on port(s): 8080 (http) with context path ''
Started Application in X.XXX seconds
```

### Step 2: Open Dashboard
Navigate to: **http://localhost:8080/dashboard.html**

**UI Layout:**
```
┌─────────────────────────────────────────────────────────────┐
│  🚀 API Test Generator                                    │
│  Configure your API and run automated tests                 │
├─────────────────────────────────────────────────────────────┤
│  Endpoint URL *  [_____________________________________]   │
│  HTTP Method     [▼ POST]                                  │
│  Auth Type       [▼ No Auth]                               │
│  Client ID       [_____________________________]          │
│  Client Secret   [_____________________________]          │
│  Request Body    [_____________________________________]   │
│                 [_____________________________________]   │
│                 [_____________________________________]   │
│                                                           │
│  [▶ Generate & Run Tests]                                 │
└─────────────────────────────────────────────────────────────┘
```

---

### Step 3A: Test GET Method (No Authentication)

**Configuration:**
1. **Endpoint URL**: `https://jsonplaceholder.typicode.com/posts`
2. **HTTP Method**: `GET` (select from dropdown)
3. **Authentication Type**: `No Auth` (select from dropdown)
4. **Request Body**: Leave empty (not needed for GET)

**Click**: `▶ Generate & Run Tests`

**Expected Results:**
```
┌─────────────────────────────────────────────────────────────┐
│  Test Results                                               │
├─────────────────────────────────────────────────────────────┤
│  ✅ 2/2 tests passed                                        │
│                                                             │
│  ┌────────────┬────────┬────────┬────────┬────────┐        │
│  │ Endpoint   │ Method │ Status │ Code   │ Duration│        │
│  ├────────────┼────────┼────────┼────────┼────────┤        │
│  │ /posts     │ GET    │ PASS   │ 200    │ 120ms  │        │
│  │ /posts     │ GET    │ PASS   │ 200    │ 95ms   │        │
│  └────────────┴────────┴────────┴────────┴────────┘        │
└─────────────────────────────────────────────────────────────┘
```

---

### Step 3B: Test POST Method (Basic Authentication)

**Configuration:**
1. **Endpoint URL**: `http://localhost:8089/api/v1/products`
2. **HTTP Method**: `POST` (select from dropdown)
3. **Authentication Type**: `Basic Auth` (select from dropdown)
4. **Client ID**: `test-client`
5. **Client Secret**: `test-secret`
6. **Request Body**:
```json
{
  "name": "Test Product",
  "price": 99.99,
  "category": "Electronics"
}
```

**Click**: `▶ Generate & Run Tests`

**Expected Results:**
```
┌─────────────────────────────────────────────────────────────┐
│  Test Results                                               │
├─────────────────────────────────────────────────────────────┤
│  ✅ 3/3 tests passed                                        │
│                                                             │
│  ┌─────────────────────────┬────────┬────────┬──────────┐  │
│  │ Endpoint                │ Method │ Status │ Code     │  │
│  ├─────────────────────────┼────────┼────────┼──────────┤  │
│  │ /api/v1/products        │ POST   │ PASS   │ 201      │  │
│  │ /api/v1/products        │ POST   │ FAIL   │ 400      │  │
│  │ /api/v1/products        │ POST   │ PASS   │ 401      │  │
│  └─────────────────────────┴────────┴────────┴──────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

### Step 3C: Test with API Key Authentication

**Configuration:**
1. **Endpoint URL**: `https://api.example.com/v1/data`
2. **HTTP Method**: `GET`
3. **Authentication Type**: `API Key`
4. **Client ID**: `X-API-Key` (header name)
5. **Client Secret**: `your-api-key-here`
6. **Request Body**: Leave empty

**Click**: `▶ Generate & Run Tests`

---

## 📊 Excel Export

Test results are automatically saved to:
```
target/<sanitized-endpoint-name>-test-results.xlsx
```

### Excel Columns
| Column | Description |
|--------|-------------|
| Endpoint | Full API URL |
| Method | HTTP method |
| Status | PASS/FAIL/ERROR |
| Status Code | HTTP code |
| Response Body | Response content |
| Issue | Error description |

---

## 🎯 Quick Reference

| Action | Code |
|--------|------|
| Add Basic Auth | Select "Basic Auth", enter Client ID/Secret |
| Add API Key | Select "API Key", enter Header Name/Value |
| Test GET | Select GET method, leave body empty |
| Test POST | Select POST method, add JSON body |
| No Auth | Select "No Auth" or leave fields empty |

---

## 🔄 CI/CD Integration

```bash
# Run tests in pipeline
mvn test -Dtest=CustomerApiTestMuleSoft49

# With environment
mvn test -Dtest=CustomerApiTestMuleSoft49 -Pdev
```

---

## 📝 License

Internal use for MuleSoft API testing.