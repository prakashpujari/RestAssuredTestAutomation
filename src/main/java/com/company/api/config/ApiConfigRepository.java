package com.company.api.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiConfigRepository extends JpaRepository<ApiEntity, Long> {
    List<ApiEntity> findByActiveTrue();
    List<ApiEntity> findByNameContainingIgnoreCase(String name);
}

interface TestResultRepository extends JpaRepository<TestResultEntity, Long> {
    List<TestResultEntity> findTop50ByOrderByIdDesc();
}