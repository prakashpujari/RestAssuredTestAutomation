package com.company.api.config;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_results")
public class TestResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String apiName;
    private String methodName;
    private String endpoint;
    private String status; // PASS, FAIL, ERROR

    @Column(length = 2000)
    private String requestDetails;

    @Column(length = 2000)
    private String responseDetails;

    private int assertionsExecuted;
    private int assertionsPassed;
    private int assertionsFailed;

    // Confidence Score Components
    private double responseTimeScore; // 0-1 based on expected response time
    private double schemaValidationScore; // 0-1 based on schema validation
    private double statusConsistencyScore; // 0-1 based on expected vs actual
    private double confidenceScore; // Overall combined score

    private long durationMs;
    private int statusCode;
    private LocalDateTime executedAt = LocalDateTime.now();

    // Constructors
    public TestResultEntity() {}

    public TestResultEntity(String apiName, String methodName, String endpoint) {
        this.apiName = apiName;
        this.methodName = methodName;
        this.endpoint = endpoint;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRequestDetails() { return requestDetails; }
    public void setRequestDetails(String requestDetails) { this.requestDetails = requestDetails; }
    public String getResponseDetails() { return responseDetails; }
    public void setResponseDetails(String responseDetails) { this.responseDetails = responseDetails; }
    public int getAssertionsExecuted() { return assertionsExecuted; }
    public void setAssertionsExecuted(int assertionsExecuted) { this.assertionsExecuted = assertionsExecuted; }
    public int getAssertionsPassed() { return assertionsPassed; }
    public void setAssertionsPassed(int assertionsPassed) { this.assertionsPassed = assertionsPassed; }
    public int getAssertionsFailed() { return assertionsFailed; }
    public void setAssertionsFailed(int assertionsFailed) { this.assertionsFailed = assertionsFailed; }
    public double getResponseTimeScore() { return responseTimeScore; }
    public void setResponseTimeScore(double responseTimeScore) { this.responseTimeScore = responseTimeScore; }
    public double getSchemaValidationScore() { return schemaValidationScore; }
    public void setSchemaValidationScore(double schemaValidationScore) { this.schemaValidationScore = schemaValidationScore; }
    public double getStatusConsistencyScore() { return statusConsistencyScore; }
    public void setStatusConsistencyScore(double statusConsistencyScore) { this.statusConsistencyScore = statusConsistencyScore; }
    public double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}