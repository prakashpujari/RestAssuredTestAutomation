package com.company.api.controller;

import com.company.api.model.TestDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for managing test definitions.
 */
@RestController
@RequestMapping("/api/tests")
public class TestController {
    private static final Path TEST_DEFINITIONS_DIR = Paths.get("src/main/resources/test-definitions");

    public TestController() throws IOException {
        if (!Files.exists(TEST_DEFINITIONS_DIR)) {
            Files.createDirectories(TEST_DEFINITIONS_DIR);
        }
    }

    @GetMapping
    public ResponseEntity<List<TestDefinition>> list() throws IOException {
        List<TestDefinition> list = Files.list(TEST_DEFINITIONS_DIR)
                .filter(p -> p.toString().endsWith(".json"))
                .map(this::readDefinition)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<TestDefinition> create(@RequestBody TestDefinition definition) throws IOException {
        if (definition.getId() == null || definition.getId().isEmpty()) {
            definition.setId(UUID.randomUUID().toString());
        }
        Path target = TEST_DEFINITIONS_DIR.resolve(definition.getId() + ".json");
        Files.writeString(target, toJson(definition), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return ResponseEntity.ok(definition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) throws IOException {
        Path target = TEST_DEFINITIONS_DIR.resolve(id + ".json");
        Files.deleteIfExists(target);
        return ResponseEntity.noContent().build();
    }

    private TestDefinition readDefinition(Path path) {
        try {
            String json = Files.readString(path);
            return fromJson(json);
        } catch (IOException e) {
            return null;
        }
    }

    // Simple JSON serialization/deserialization using Jackson ObjectMapper (available via Spring Boot)
    private static final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

    private String toJson(TestDefinition def) throws IOException {
        return mapper.writeValueAsString(def);
    }

    private TestDefinition fromJson(String json) throws IOException {
        return mapper.readValue(json, TestDefinition.class);
    }
}
