package com.company.api.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads API definitions from YAML configuration files.
 * Supports environment-specific configurations and test case generation.
 */
public class ApiDefinitionLoader {

    private static ApiDefinitionLoader instance;
    private Map<String, Object> apiConfig;
    private Map<String, Object> envConfig;

    private ApiDefinitionLoader() {
        loadConfigs();
    }

    public static ApiDefinitionLoader getInstance() {
        if (instance == null) {
            instance = new ApiDefinitionLoader();
        }
        return instance;
    }

    private void loadConfigs() {
        Yaml yaml = new Yaml();

        // Load API definitions
        try (InputStream apiStream = getClass().getClassLoader()
                .getResourceAsStream("config/apis.yaml")) {
            apiConfig = yaml.loadAs(apiStream, HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load api definitions: " + e.getMessage(), e);
        }

        // Load environment configurations
        try (InputStream envStream = getClass().getClassLoader()
                .getResourceAsStream("config/environments.yaml")) {
            envConfig = yaml.loadAs(envStream, HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load environment config: " + e.getMessage(), e);
        }
    }

    /**
     * Gets all API definitions.
     */
    public List<Map<String, Object>> getApiDefinitions() {
        return (List<Map<String, Object>>) apiConfig.get("apis");
    }

    /**
     * Gets environment configuration by name.
     */
    public Map<String, String> getEnvironmentConfig(String envName) {
        Map<String, Map<String, String>> environments =
                (Map<String, Map<String, String>>) apiConfig.get("environments");
        if (environments == null) {
            return Map.of();
        }
        return environments.getOrDefault(envName, Map.of());
    }

    /**
     * Gets execution configuration for environment.
     */
    public Map<String, Object> getExecutionConfig(String envName) {
        Map<String, Map<String, Object>> envs =
                (Map<String, Map<String, Object>>) envConfig.get("environments");
        Map<String, Object> defaults = (Map<String, Object>) envConfig.get("default");
        Map<String, Object> envSpecific = envs == null ? null : envs.get(envName);

        // Merge with defaults
        Map<String, Object> merged = new HashMap<>(defaults);
        if (envSpecific != null) {
            merged.putAll(envSpecific);
        }
        return merged;
    }

    /**
     * Resolves a value, checking for ${env:VAR_NAME} syntax.
     */
    public String resolveValue(String value) {
        if (value != null && value.startsWith("${env:")) {
            String envVar = value.substring(6, value.length() - 1);
            return System.getenv(envVar);
        }
        return value;
    }
}