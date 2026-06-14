package com.company.api.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResultEntity, Long> {
    List<TestResultEntity> findTop50ByOrderByIdDesc();
}