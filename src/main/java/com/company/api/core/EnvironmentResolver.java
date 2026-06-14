package com.company.api.core;

/**
 * Utility to resolve the base URL for API calls based on environment settings.
 * It first checks the system property `env.url` which is set by TestExecutorService
 * when a particular test definition specifies an environment.  If that property
 * is not present, it falls back to the `env.name` property and maps common
 * logical names to the URLs defined in the Maven profile section.
 *
 * {@code env.url} takes precedence because it allows launching the test
 * runner against a custom address (e.g. WireMock or a deployed staging API)
 * without modifying the code.
 */
public class EnvironmentResolver {
    public static String getBaseUrl() {
        String url = System.getProperty("env.url");
        if (url != null && !url.isBlank()) {
            return url;
        }
        String envName = System.getProperty("env.name");
        if (envName == null || envName.isBlank()) {
            return "http://localhost:8089"; // default WireMock
        }
        return switch (envName.toLowerCase()) {
            case "local" -> "http://localhost:8089";
            case "dev" -> "https://dev-api.company.com/customers";
            case "qa" -> "https://qa-api.company.com/customers";
            case "uat" -> "https://uat-api.company.com/customers";
            case "prod" -> "https://api.company.com/customers";
            default -> "http://localhost:8089";
        };
    }
}
