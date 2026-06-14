# Scaling to 100+ APIs - Architecture Guide

## Quick Start

1. **Add your API to config/apis.yaml** - Just add endpoints and basic test definitions
2. **Add test data templates** to `src/test/resources/testdata/templates/`
3. **Run tests** - Dynamic tests auto-generate from config

## Architecture

```
┌─────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  UI Dashboard   │────▶│ Config Service     │────▶│ Yaml Definitions │
└─────────────────┘     └──────────────────┘     └──────────────────┘
                                │
                                ▼
┌─────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  CI/CD Trigger  │────▶│ Dynamic Tests     │────▶│ Real APIs        │
└─────────────────┘     └──────────────────┘     └──────────────────┘
```

## How It Works

### 1. Configuration Layer (`apis.yaml`)
Add any number of APIs with minimal configuration:

```yaml
apis:
  - name: your-api-name
    basePath: /api/v1/your-api
    endpoints:
      - method: GET|POST|PUT|DELETE
        path: /resource/{id}
        tests:
          - name: test-name
            type: positive|negative|security
            expectedStatus: 200
```

### 2. Dynamic Test Generation
The `DynamicApiTest` class reads the YAML and generates tests automatically:
- No need to write individual test classes
- Just update the config file
- Tests run in parallel for performance

### 3. CI/CD Integration
GitHub Actions workflow (`api-tests.yml`) provides:
- Parallel module execution
- Environment selection via workflow_dispatch
- Slack notifications
- Allure reports

## UI Configuration

### Backend API Endpoints
```
GET  /api/test-config/apis           - List all APIs
GET  /api/test-config/apis/{name}    - Get API config
POST /api/test-config/run            - Trigger test run
GET  /api/test-config/run/{jobId}    - Get test status
```

### Frontend Dashboard (React Example)
```javascript
// Fetch and display available APIs
const apis = await fetch('/api/test-config/apis');

// Trigger tests for specific APIs
await fetch('/api/test-config/run', {
  method: 'POST',
  body: JSON.stringify({
    apis: ['customers', 'orders'],
    environment: 'qa',
    parallel: true
  })
});
```

## Scaling Strategies

### Parallel Execution
```bash
# Module-level parallel
mvn test -Dparallel=classes -DthreadCount=10

# Method-level parallel
mvn test -Dparallel=methods -DthreadCount=20
```

### Test Categorization
- `positive` - Happy path tests
- `negative` - Error handling tests
- `security` - Auth, authorization tests
- `policy` - CORS, rate limiting tests

Run specific categories:
```bash
mvn test -Dgroups=positive,negative
```

### Environment Profiles
```bash
# Local (WireMock)
mvn test -Dtest.env=local

# Dev
mvn test -Dtest.env=dev

# QA
mvn test -Dtest.env=qa -Dparallel=methods
```

## Adding a New API (5 minutes)

1. Add to `apis.yaml`:
```yaml
  - name: products
    basePath: /products
    endpoints:
      - method: GET
        path: /products
        tests:
          - name: list-products
            type: positive
            expectedStatus: 200
```

2. Add test data template:
```json
// src/test/resources/testdata/templates/product.json
{
  "name": "Product {{id}}",
  "price": 99.99,
  "sku": "SKU-{{id}}"
}
```

3. Run tests - it just works!

## Performance Optimizations

| Feature | Benefit |
|---------|---------|
| Parallel execution | 10x faster with 20 threads |
| WireMock stubbing | No real API dependencies |
| Bulk test data loading | Reduced I/O |
| Retry filter | Handles flaky endpoints |

## Monitoring

### Test Results Dashboard
- Allure reports auto-generated
- Slack notifications on failure
- API for querying test history

### Metrics Collected
- Response times per endpoint
- Success/failure rates
- Performance trends

## Next Steps

1. ✅ Add Jackson dependency for JSON processing
2. ✅ Create UI dashboard (React/Vue component)
3. ✅ Add database for test history
4. ✅ Add performance testing integration
5. ✅ Add contract testing (Pact)

---

**Ready to scale?** Just start adding APIs to `apis.yaml`!