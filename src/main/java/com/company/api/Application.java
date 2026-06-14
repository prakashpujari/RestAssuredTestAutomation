package com.company.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Rest Assured test automation service.
 * Exposes REST endpoints for managing test definitions, executing tests,
 * and generating tests via the Groq LLM.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
