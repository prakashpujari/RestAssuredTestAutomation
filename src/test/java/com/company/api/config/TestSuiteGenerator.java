package com.company.api.config;

import com.company.api.models.ApiEndpoint;
import com.company.api.models.TestCaseConfig;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Generates dynamic test suites based on YAML configuration.
 * This class provides a @TestFactory that creates tests for all APIs defined in config.
 */
public class TestSuiteGenerator {

    private final ApiDefinitionLoader loader = ApiDefinitionLoader.getInstance();

    /**
     * Generates a stream of dynamic tests for all configured APIs.
     * Usage in your test class:
     *
     * @TestFactory
     * Stream<DynamicTest> generateApiTests() {
     *     return new TestSuiteGenerator().createAllTests();
     * }
     */
    public Stream<DynamicTest> createAllTests() {
        List<DynamicTest> tests = new ArrayList<>();

        loader.getApiDefinitions().forEach(api -> {
            String apiName = (String) api.get("name");
            String basePath = (String) api.get("basePath");
            List<Map<String, Object>> endpoints = (List<Map<String, Object>>) api.get("endpoints");

            endpoints.forEach(endpoint -> {
                String method = (String) endpoint.get("method");
                String path = (String) endpoint.get("path");
                List<Map<String, Object>> testCases = (List<Map<String, Object>>) endpoint.get("tests");

                testCases.forEach(testCase -> {
                    String testName = (String) testCase.get("name");
                    Integer expectedStatus = (Integer) testCase.get("expectedStatus");
                    String testType = (String) testCase.get("type");

                    String testDisplayName = String.format("%s - %s %s [%s]",
                            apiName, method, path, testName);

                    tests.add(dynamicTest(testDisplayName, () ->
                        executeTest(apiName, basePath, method, path, expectedStatus, testType, testCase)));
                });
            });
        });

        return tests.stream();
    }

    /**
     * Creates dynamic tests for a specific API module.
     */
    public Stream<DynamicTest> createTestsForApi(String apiName) {
        List<DynamicTest> tests = new ArrayList<>();

        loader.getApiDefinitions().stream()
            .filter(api -> apiName.equals(api.get("name")))
            .forEach(api -> {
                String basePath = (String) api.get("basePath");
                List<Map<String, Object>> endpoints = (List<Map<String, Object>>) api.get("endpoints");

                endpoints.forEach(endpoint -> {
                    String method = (String) endpoint.get("method");
                    String path = (String) endpoint.get("path");
                    List<Map<String, Object>> testCases = (List<Map<String, Object>>) endpoint.get("tests");

                    testCases.forEach(testCase -> {
                        String testName = (String) testCase.get("name");
                        Integer expectedStatus = (Integer) testCase.get("expectedStatus");
                        String testType = (String) testCase.get("type");

                        String testDisplayName = String.format("%s - %s %s [%s]",
                                apiName, method, path, testName);

                        tests.add(dynamicTest(testDisplayName, () ->
                            executeTest(apiName, basePath, method, path, expectedStatus, testType, testCase)));
                    });
                });
            });

        return tests.stream();
    }

    private void executeTest(String apiName, String basePath, String method, String path,
                          Integer expectedStatus, String testType, Map<String, Object> testCaseConfig) {
        // Placeholder - actual implementation uses RestAssured
        // This would be called by the dynamic test
        System.out.printf("Executing: %s %s/%s - expecting %d%n",
                method, basePath, path, expectedStatus);
    }

    /**
     * Creates test methods dynamically using reflection.
     * Useful for generating separate test classes per API.
     */
    public void generateTestClasses() {
        // This would generate .java files during build time
        // See the maven plugin configuration below
    }
}