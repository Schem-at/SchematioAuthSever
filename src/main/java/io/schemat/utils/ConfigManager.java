package io.schemat.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "config.properties";
    private static final String JWT_SECRET_ENV = "JWT_SECRET";
    private static final String API_ENDPOINT_ENV = "API_ENDPOINT";
    private JsonObject configData;

    private String jwtSecret;
    private String apiEndpoint;

    public ConfigManager() {
        loadConfig();
    }

    private boolean loadConfigFromProperties() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return false;
        }
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            configData = GSON.fromJson(reader, JsonObject.class);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadConfig() {
        if (loadConfigFromProperties()) {
            jwtSecret = configData.get(JWT_SECRET_ENV).getAsString();
            apiEndpoint = configData.get(API_ENDPOINT_ENV).getAsString();
            return;
        }
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