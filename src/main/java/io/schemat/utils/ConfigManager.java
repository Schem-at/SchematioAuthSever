package io.schemat.utils;

public class ConfigManager {
    private static final String JWT_SECRET_ENV = "JWT_SECRET";
    private static final String API_ENDPOINT_ENV = "API_ENDPOINT";

    private String jwtSecret;
    private String apiEndpoint;

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        jwtSecret = System.getenv(JWT_SECRET_ENV);
        apiEndpoint = System.getenv(API_ENDPOINT_ENV);

        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new RuntimeException("JWT_SECRET environment variable is not set");
        }

        if (apiEndpoint == null || apiEndpoint.isEmpty()) {
            throw new RuntimeException("API_ENDPOINT environment variable is not set");
        }
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }
}