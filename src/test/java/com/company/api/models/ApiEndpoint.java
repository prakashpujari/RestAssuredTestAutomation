package com.company.api.models;

import java.util.List;
import java.util.Map;

/**
 * Model representing an API endpoint definition from YAML config.
 */
public class ApiEndpoint {
    private String method;
    private String path;
    private String description;
    private List<TestCaseConfig> tests;
    private Map<String, String> headers;
    private String bodyTemplate;

    public ApiEndpoint() {}

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<TestCaseConfig> getTests() { return tests; }
    public void setTests(List<TestCaseConfig> tests) { this.tests = tests; }

    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }

    public String getBodyTemplate() { return bodyTemplate; }
    public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }
}