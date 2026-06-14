package com.company.api.models;

import java.util.Map;

/**
 * Model representing a test case configuration from YAML config.
 */
public class TestCaseConfig {
    private String name;
    private String type; // positive, negative, policy, security
    private int expectedStatus;
    private String bodyTemplate;
    private Map<String, String> overrides; // Override headers, params for specific test

    public TestCaseConfig() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getExpectedStatus() { return expectedStatus; }
    public void setExpectedStatus(int expectedStatus) { this.expectedStatus = expectedStatus; }

    public String getBodyTemplate() { return bodyTemplate; }
    public void setBodyTemplate(String bodyTemplate) { this.bodyTemplate = bodyTemplate; }

    public Map<String, String> getOverrides() { return overrides; }
    public void setOverrides(Map<String, String> overrides) { this.overrides = overrides; }
}