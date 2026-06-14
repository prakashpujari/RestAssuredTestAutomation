package com.company.api.config;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "api_configs")
public class ApiEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String baseUrl;
    private String basePath;
    private String authType;
    private String clientId;
    private String clientSecret;
    private String apiKeyHeader;
    private String apiKeyValue;
    private String tokenUrl;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    // For endpoints - stored as JSON string
    @Column(length = 1000)
    private String endpoints;

    // Constructors
    public ApiEntity() {}

    public ApiEntity(String name, String baseUrl, String basePath) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.basePath = basePath;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getBasePath() { return basePath; }
    public void setBasePath(String basePath) { this.basePath = basePath; }
    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getApiKeyHeader() { return apiKeyHeader; }
    public void setApiKeyHeader(String apiKeyHeader) { this.apiKeyHeader = apiKeyHeader; }
    public String getApiKeyValue() { return apiKeyValue; }
    public void setApiKeyValue(String apiKeyValue) { this.apiKeyValue = apiKeyValue; }
    public String getTokenUrl() { return tokenUrl; }
    public void setTokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; }
    public String getEndpoints() { return endpoints; }
    public void setEndpoints(String endpoints) { this.endpoints = endpoints; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}