package com.company.api.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Builds test data dynamically from JSON templates.
 * Supports placeholder replacement like {{firstName}}, {{email}}, etc.
 */
public class DynamicTestDataBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();
    private final Map<String, String> data = new HashMap<>();

    public DynamicTestDataBuilder() {
        // Default test data
        data.put("firstName", "John");
        data.put("lastName", "Doe");
        data.put("email", "john.doe@example.com");
        data.put("phone", "123-456-7890");
        data.put("id", generateId());
    }

    public DynamicTestDataBuilder withField(String key, String value) {
        data.put(key, value);
        return this;
    }

    public DynamicTestDataBuilder withRandomEmail() {
        data.put("email", "user" + random.nextInt(10000) + "@test.com");
        return this;
    }

    public DynamicTestDataBuilder withRandomId() {
        data.put("id", generateId());
        return this;
    }

    public Map<String, Object> buildAsMap() {
        return new HashMap<>(data);
    }

    public String buildAsString() {
        return mapper.convertValue(data, String.class);
    }

    public <T> T build(Class<T> valueType) {
        return mapper.convertValue(data, valueType);
    }

    /**
     * Loads template from resources and replaces placeholders.
     */
    public static String loadTemplate(String templateName, Map<String, String> values) {
        try (InputStream stream = DynamicTestDataBuilder.class
                .getClassLoader()
                .getResourceAsStream("testdata/templates/" + templateName)) {

            if (stream == null) {
                throw new IllegalArgumentException("Template not found: " + templateName);
            }

            String content = new String(stream.readAllBytes());
            for (Map.Entry<String, String> entry : values.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            return content;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load template: " + templateName, e);
        }
    }

    private String generateId() {
        return java.util.UUID.randomUUID().toString();
    }
}