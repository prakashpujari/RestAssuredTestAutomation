package com.company.api.generator;

/**
 * API Configuration for test generation
 */
public class ApiTestConfig {
    private String endpoint;
    private String method;
    private String securityType; // BASIC_AUTH, OAUTH2, API_KEY, NONE
    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String username;
    private String password;
    private String apiKeyHeader;
    private String apiKeyName;
    private String requestBody;
    private String[] pathParams;
    private String[] queryParams;

    public ApiTestConfig() {}

    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getSecurityType() { return securityType; }
    public void setSecurityType(String securityType) { this.securityType = securityType; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
    public String getTokenUrl() { return tokenUrl; }
    public void setTokenUrl(String tokenUrl) { this.tokenUrl = tokenUrl; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getApiKeyHeader() { return apiKeyHeader; }
    public void setApiKeyHeader(String apiKeyHeader) { this.apiKeyHeader = apiKeyHeader; }
    public String getApiKeyName() { return apiKeyName; }
    public void setApiKeyName(String apiKeyName) { this.apiKeyName = apiKeyName; }
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    public String[] getPathParams() { return pathParams; }
    public void setPathParams(String[] pathParams) { this.pathParams = pathParams; }
    public String[] getQueryParams() { return queryParams; }
    public void setQueryParams(String[] queryParams) { this.queryParams = queryParams; }

    public static class Builder {
        private ApiTestConfig config = new ApiTestConfig();

        public Builder endpoint(String endpoint) { config.setEndpoint(endpoint); return this; }
        public Builder method(String method) { config.setMethod(method); return this; }
        public Builder basicAuth(String username, String password) {
            config.setSecurityType("BASIC_AUTH");
            config.setUsername(username);
            config.setPassword(password);
            return this;
        }
        public Builder oauth2(String tokenUrl, String clientId, String clientSecret) {
            config.setSecurityType("OAUTH2");
            config.setTokenUrl(tokenUrl);
            config.setClientId(clientId);
            config.setClientSecret(clientSecret);
            return this;
        }
        public Builder apiKey(String headerName, String headerValue) {
            config.setSecurityType("API_KEY");
            config.setApiKeyName(headerName);
            config.setApiKeyHeader(headerValue);
            return this;
        }
        public Builder noAuth() {
            config.setSecurityType("NONE");
            return this;
        }
        public Builder requestBody(String body) { config.setRequestBody(body); return this; }
        public Builder pathParams(String... params) { config.setPathParams(params); return this; }
        public Builder queryParams(String... params) { config.setQueryParams(params); return this; }

        public ApiTestConfig build() { return config; }
    }
}