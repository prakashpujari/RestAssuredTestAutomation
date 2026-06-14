package com.company.api.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark test methods with metadata for config-driven execution.
 * Allows tests to be categorized and filtered by the framework.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestMetadata {
    String api() default "";
    String endpoint() default "";
    String type() default "positive"; // positive, negative, security, policy, performance
    String[] tags() default {};
    int expectedStatus() default 200;
    boolean skipInCi() default false; // Skip in CI/CD pipeline
    String description() default "";
}