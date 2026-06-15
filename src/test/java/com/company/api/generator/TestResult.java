package com.company.api.generator;

/**
 * Test result for Excel export
 */
public class TestResult {
    private String endpoint;
    private String method;
    private String testInput;
    private int statusCode;
    private String responseBody;
    private String status; // PASS, FAIL, ERROR
    private String issue;

    public TestResult() {}

    public TestResult(String endpoint, String method, String testInput, int statusCode, String responseBody, String status, String issue) {
        this.endpoint = endpoint;
        this.method = method;
        this.testInput = testInput;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.status = status;
        this.issue = issue;
    }

    // Getters and Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getTestInput() { return testInput; }
    public void setTestInput(String testInput) { this.testInput = testInput; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }
}