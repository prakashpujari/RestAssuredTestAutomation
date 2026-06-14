# 🧪 QA Team Guide: API Testing Automation Framework

## Model: Claude Opus 4.8

---

## Quick Start (5 Minutes)

```bash
# 1. Clone and setup
git clone https://github.com/prakashpujari/RestAssuredTestAutomation.git
cd RestAssuredTestAutomation

# 2. Run sample tests (no setup needed - uses public APIs)
mvn test -Dtest=PublicApiTest

# 3. See results in console (look for "BUILD SUCCESS")
```

---

## 📋 Step-by-Step: How to Add Your API for Testing

### STEP 1: Add API Definition to Config File

Open: `src/test/resources/config/apis.yaml`

Add your API at the bottom (follow the pattern):

```yaml
  # ADD YOUR API HERE
  - name: products
    basePath: /products
    baseUrl: https://api.yourcompany.com  # Your API base URL
    endpoints:
      - method: GET
        path: /products
        description: List all products
        tests:
          - name: list-products-success
            type: positive
            expectedStatus: 200

      - method: POST
        path: /products
        description: Create a product
        tests:
          - name: create-product-success
            type: positive
            expectedStatus: 201
```

### STEP 2: Add Test Data Templates (Optional)

Create: `src/test/resources/testdata/templates/product-create.json`

```json
{
  "name": "{{productName}}",
  "price": {{price}},
  "sku": "{{sku}}"
}
```

### STEP 3: Run Your API Tests

```bash
# Test against your API
mvn test -Dtest.env=qa

# Or test specific API only
mvn test -Dtest=PublicApiTest#listPosts
```

### STEP 4: Check Results

```bash
# Test results appear in console automatically
# Look for: Tests run: 14, Failures: 0, Errors: 0, Skipped: 0, BUILD SUCCESS

# View detailed XML reports:
# target/surefire-reports/TEST-*.xml
```

### STEP 4b: Run UI Dashboard (Optional)

```bash
# Spring Boot app runs on port 8085 (to avoid conflicts)
mvn spring-boot:run

# Open in browser
http://localhost:8085/dashboard.html
```

### 📊 Dashboard Features

The dashboard shows **complete test visibility**:

| Feature | What You See |
|---------|-------------|
| **API Names** | JSONPlaceholder, HTTPBin, etc. |
| **Endpoints** | GET /posts, POST /users, etc. |
| **Status** | ✅ PASS / ❌ FAIL for each test |
| **Assertions** | Number of validations per test |
| **Response** | Sample response data |
| **Duration** | Time taken per test |
| **Accuracy** | Overall pass rate percentage |

---

## 🎯 Test Types Explained

| Type | What It Tests | Example Status |
|------|---------------|----------------|
| `positive` | Happy path (success cases) | 200, 201, 204 |
| `negative` | Error cases (bad input) | 400, 404, 500 |
| `security` | Auth, permissions | 401, 403 |

---

## 🚀 Running Tests in Different Environments

```bash
# Local testing (with WireMock mocks)
mvn test -Dtest.env=local

# Development environment
mvn test -Dtest.env=dev

# QA environment
mvn test -Dtest.env=qa

# Production (read-only tests recommended)
mvn test -Dtest.env=prod -Dtest="SafeReadTests"
```

---

## 📊 Running Tests for 100+ APIs

### Parallel Execution (Fast!)

```bash
# Run all APIs in parallel (20 threads)
mvn test -Dtest.env=qa -Dparallel=methods -DthreadCount=20

# Run specific modules in parallel
mvn test -Dtest="ProductsTest,OrdersTest,UsersTest"
```

### Selective Testing

```bash
# Run only positive tests
mvn test -Dgroups=positive

# Run only security tests
mvn test -Dgroups=security

# Run specific API
mvn test -Dtest="ProductsApiTest"
```

---

## 🖥️ UI Dashboard Usage

### Start the Dashboard

```bash
# Run the configuration server
mvn spring-boot:run
```

Then open: **http://localhost:8080**

### From the Dashboard You Can:
1. ✅ See all available APIs
2. ✅ Select which APIs to test
3. ✅ Choose environment (local/dev/qa/prod)
4. ✅ Trigger tests with one click
5. ✅ View test status and results

---

## 📝 Example: Complete API Addition

### 1. Add to `apis.yaml`:

```yaml
  - name: users-api
    basePath: /api/v1
    baseUrl: https://reqres.in/api
    endpoints:
      - method: GET
        path: /users
        tests:
          - name: get-users
            type: positive
            expectedStatus: 200
            queryParams:
              page: 1
      
      - method: GET
        path: /users/{id}
        tests:
          - name: get-user-by-id
            type: positive
            expectedStatus: 200
          
          - name: get-user-notfound
            type: negative
            expectedStatus: 404
            
      - method: POST
        path: /users
        tests:
          - name: create-user
            type: positive
            expectedStatus: 201
```

### 2. Run Tests

```bash
mvn test -Dtest=DynamicApiTest
```

---

## 🔧 Environment Variables

Set these in your CI/CD or terminal:

```bash
# For QA environment
export CLIENT_ID_QA="your-qa-client-id"
export CLIENT_SECRET_QA="your-qa-client-secret"

# For Production
export CLIENT_ID_PROD="your-prod-client-id"
export CLIENT_SECRET_PROD="your-prod-client-secret"
```

---

## 📈 CI/CD Integration

Tests automatically run in GitHub Actions when you:
- Push code to GitHub
- Create a Pull Request
- Nightly at 2 AM

To trigger manually:
1. Go to GitHub → Actions tab
2. Select "API Test Suite" workflow
3. Click "Run workflow"
4. Choose environment (qa, dev, etc.)

---

## ❓ Troubleshooting

### "Failed to load class SLF4J"
```bash
# This is just a warning, tests still work
# Add slf4j dependency to pom.xml if needed
```

### "UnknownHostException"
```bash
# Check your internet connection
# Verify the API URL is accessible
```

### "Tests timing out"
```bash
# Increase timeout in apis.yaml
# Or add: -Drestassured.timeout=60000
```

---

## 📞 Quick Reference

| Task | Command |
|------|---------|
| Run all tests | `mvn test` |
| Run specific API | `mvn test -Dtest=PublicApiTest` |
| Run with QA config | `mvn test -Dtest.env=qa` |
| Parallel execution | `mvn test -Dparallel=true -DthreadCount=20` |
| Start UI dashboard | `mvn spring-boot:run` |
| Open dashboard | `http://localhost:8085` |
| Clean and rebuild | `mvn clean test` |

---

**That's it!** Just add your API to `apis.yaml` and run tests. The framework handles the rest automatically.