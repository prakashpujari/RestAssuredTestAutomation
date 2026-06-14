package com.company.api.controller;

import com.company.api.model.TestRunResult;
import com.company.api.service.TestExecutorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

/**
 * REST controller for executing tests and retrieving results.
 */
@RestController
@RequestMapping("/api/run")
public class RunController {
    private final TestExecutorService executorService = new TestExecutorService();

    @PostMapping
    public ResponseEntity<List<TestRunResult>> runAll() throws IOException {
        List<TestRunResult> results = executorService.runAll();
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{id}")
    public ResponseEntity<TestRunResult> runOne(@PathVariable String id) throws IOException {
        TestRunResult result = executorService.run(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<List<TestRunResult>> getResults() throws IOException {
        List<TestRunResult> results = executorService.readResults();
        return ResponseEntity.ok(results);
    }
}
