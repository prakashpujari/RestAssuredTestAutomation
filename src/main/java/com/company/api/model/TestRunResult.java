package com.company.api.model;

/**
 * Result of a test execution.
 */
public class TestRunResult {
    private String testId;
    private boolean success;
    private long durationMs;
    private String errors; // optional error messages
    private String rawResponse;

    public TestRunResult() {}

    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public String getErrors() { return errors; }
    public void setErrors(String errors) { this.errors = errors; }
    public String getRawResponse() { return rawResponse; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }
}
