package com.company.api.service;

import com.company.api.model.TestDefinition;
import com.company.api.model.TestRunResult;
import com.company.api.core.EnvironmentResolver;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that loads TestDefinition JSON files and executes them using Rest Assured.
 * Results are written to {@code target/restassured-results.json} as a JSON array.
 */
public class TestExecutorService {
    private static final Path DEFINITIONS_DIR = Paths.get("src/main/resources/test-definitions");
    private static final Path RESULT_FILE = Paths.get("target/restassured-results.json");
    private final ObjectMapper mapper = new ObjectMapper();

    public TestExecutorService() {
        // Ensure directories exist
        try {
            if (!Files.exists(DEFINITIONS_DIR)) {
                Files.createDirectories(DEFINITIONS_DIR);
            }
            if (!Files.exists(RESULT_FILE.getParent())) {
                Files.createDirectories(RESULT_FILE.getParent());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize test executor directories", e);
        }
    }

    /** Run all test definitions and return results. */
    public List<TestRunResult> runAll() throws IOException {
        List<TestDefinition> defs = loadAllDefinitions();
        List<TestRunResult> results = new ArrayList<>();
        for (TestDefinition def : defs) {
            results.add(execute(def));
        }
        writeResults(results);
        return results;
    }

    /** Run a single test by id. */
    public TestRunResult run(String id) throws IOException {
        Optional<TestDefinition> opt = loadAllDefinitions().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Test definition not found: " + id);
        }
        TestRunResult result = execute(opt.get());
        // Append to existing results file
        List<TestRunResult> existing = readResults();
        existing.add(result);
        writeResults(existing);
        return result;
    }

    /** Read accumulated results from file (empty list if none). */
    public List<TestRunResult> readResults() throws IOException {
        if (!Files.exists(RESULT_FILE)) {
            return new ArrayList<>();
        }
        String json = Files.readString(RESULT_FILE);
        return mapper.readValue(json, new TypeReference<List<TestRunResult>>() {});
    }

    private List<TestDefinition> loadAllDefinitions() throws IOException {
        if (!Files.exists(DEFINITIONS_DIR)) {
            return new ArrayList<>();
        }
        return Files.list(DEFINITIONS_DIR)
                .filter(p -> p.toString().endsWith(".json"))
                .map(this::readDefinition)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private TestDefinition readDefinition(Path path) {
        try {
            String json = Files.readString(path);
            return mapper.readValue(json, TestDefinition.class);
        } catch (IOException e) {
            return null;
        }
    }

    private TestRunResult execute(TestDefinition def) {
        TestRunResult result = new TestRunResult();
        result.setTestId(def.getId());
        Instant start = Instant.now();
        try {
            // Ensure environment URL is set for this execution
            System.setProperty("env.url", resolveBaseUrl(def.getEnvironment()));
            // Configure RestAssured base URI from resolver
            RestAssured.baseURI = EnvironmentResolver.getBaseUrl();
            // Build request
            var req = RestAssured.given();
            if (def.getHeaders() != null) {
                req.headers(def.getHeaders());
            }
            if (def.getQueryParams() != null) {
                req.queryParams(def.getQueryParams());
            }
            if (def.getBody() != null && !def.getBody().isBlank()) {
                req.body(def.getBody());
                req.contentType("application/json");
            }
            // Send request based on method
            Response resp;
            switch (def.getMethod().toUpperCase()) {
                case "GET":
                    resp = req.get(def.getPath());
                    break;
                case "POST":
                    resp = req.post(def.getPath());
                    break;
                case "PUT":
                    resp = req.put(def.getPath());
                    break;
                case "DELETE":
                    resp = req.delete(def.getPath());
                    break;
                case "PATCH":
                    resp = req.patch(def.getPath());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + def.getMethod());
            }
            // Validate status
            boolean statusOk = resp.getStatusCode() == def.getExpectedStatus();
            // Basic JSON field validation (exact match of top‑level fields if provided)
            boolean jsonOk = true;
            if (def.getExpectedJson() != null && !def.getExpectedJson().isEmpty()) {
                Map<String, Object> bodyMap = resp.jsonPath().getMap("$");
                for (Map.Entry<String, Object> entry : def.getExpectedJson().entrySet()) {
                    if (!Objects.equals(bodyMap.get(entry.getKey()), entry.getValue())) {
                        jsonOk = false;
                        break;
                    }
                }
            }
            result.setSuccess(statusOk && jsonOk);
            if (!result.isSuccess()) {
                StringBuilder sb = new StringBuilder();
                if (!statusOk) sb.append("Unexpected status ").append(resp.getStatusCode()).append(" (expected ").append(def.getExpectedStatus()).append(") ");
                if (!jsonOk) sb.append("JSON body mismatch");
                result.setErrors(sb.toString().trim());
            }
            result.setRawResponse(resp.asString());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrors(e.getMessage());
            result.setRawResponse("");
        } finally {
            result.setDurationMs(Duration.between(start, Instant.now()).toMillis());
        }
        return result;
    }

    private String resolveBaseUrl(String environment) {
        // Simple mapping – use system property if set, otherwise fallback to default local WireMock URL
        String url = System.getProperty("env.url");
        if (url != null && !url.isBlank()) {
            return url;
        }
        // Default to localhost WireMock
        return "http://localhost:8089";
    }

    private void writeResults(List<TestRunResult> results) throws IOException {
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(results);
        Files.writeString(RESULT_FILE, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
