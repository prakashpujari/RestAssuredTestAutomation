package com.company.api.core;

/**
 * Resolves environment-specific configuration including base URLs.
 * For testing, returns the WireMock URL when the 'env.url' system property is set.
 * Otherwise, returns a default WireMock URL.
 */
public class EnvironmentResolver {

    private static final String ENV_PROPERTY = "env";
    private static final String URL_PROPERTY = "env.url";

    /**
     * Gets the base URL for the configured environment.
     * @return Base URL string
     */
    public static String getBaseUrl() {
        String url = System.getProperty(URL_PROPERTY);

        if (url != null && !url.isEmpty()) {
            System.out.println("EnvironmentResolver.getBaseUrl(): returning url system property: " + url);
            return url;
        }

        // Fallback to default WireMock URL (should be set by BaseApiTest in tests)
        System.out.println("EnvironmentResolver.getBaseUrl(): returning fallback WireMock URL: http://localhost:8089");
        return "http://localhost:8089";
    }

    /**
     * Gets the environment name.
     * @return Environment name
     */
    public static String getEnvironmentName() {
        return System.getProperty(ENV_PROPERTY, "local");
    }
}