package com.company.api.core;

/**
 * Resolves environment-specific configuration including base URLs.
 * Reads from Maven profiles or system properties.
 */
public class EnvironmentResolver {

    private static final String DEFAULT_ENV = "local";
    private static final String ENV_PROPERTY = "env";
    private static final String URL_PROPERTY = "env.url";

    /**
     * Gets the base URL for the configured environment.
     * @return Base URL string
     */
    public static String getBaseUrl() {
        String env = System.getProperty(ENV_PROPERTY, DEFAULT_ENV);
        String url = System.getProperty(URL_PROPERTY);

        if (url != null && !url.isEmpty()) {
            System.out.println("EnvironmentResolver.getBaseUrl(): returning url system property: " + url);
            return url;
        }

        // Fallback to default based on env
        String lowerEnv = env.toLowerCase();
        if ("local".equals(lowerEnv)) {
            System.out.println("EnvironmentResolver.getBaseUrl(): returning local default: http://localhost:8081");
            return "http://localhost:8081";
        } else if ("dev".equals(lowerEnv)) {
            System.out.println("EnvironmentResolver.getBaseUrl(): returning dev default: https://dev-api.company.com");
            return "https://dev-api.company.com";
        } else if ("qa".equals(lowerEnv)) {
            System.out.println("EnvironmentResolver.getBaseUrl(): returning qa default: https://qa-api.company.com");
            return "https://qa-api.company.com";
        } else if ("uat".equals(lowerEnv)) {
            System.out.println("EnvironmentResolver.getBaseUrl(): returning uat default: https://uat-api.company.com");
            return "https://uat-api.company.com";
        } else {
            System.out.println("EnvironmentResolver.getBaseUrl(): returning fallback default: http://localhost:8081");
            return "http://localhost:8081";
        }
    }

    /**
     * Gets the environment name.
     * @return Environment name
     */
    public static String getEnvironmentName() {
        return System.getProperty(ENV_PROPERTY, DEFAULT_ENV);
    }
}