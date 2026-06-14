package com.company.api.model;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a test definition that can be created via the UI or CLI.
 */
public class TestDefinition {
    private String id; // UUID string
    private String name;
    private String method; // GET, POST, etc.
    private String path; // endpoint path
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body; // JSON body as string
    private int expectedStatus;
    private Map<String, Object> expectedJson; // expected fields/values
    private String environment; // local, dev, qa, uat, prod

    public TestDefinition() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    public Map<String, String> getQueryParams() { return queryParams; }
    public void setQueryParams(Map<String, String> queryParams) { this.queryParams = queryParams; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public int getExpectedStatus() { return expectedStatus; }
    public void setExpectedStatus(int expectedStatus) { this.expectedStatus = expectedStatus; }
    public Map<String, Object> getExpectedJson() { return expectedJson; }
    public void setExpectedJson(Map<String, Object> expectedJson) { this.expectedJson = expectedJson; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
}
