package io.schemat.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "config.properties";
    private static final JsonObject DEFAULT_CONFIG = new JsonObject();
    static {
        DEFAULT_CONFIG.addProperty("jwt.secret", "your-jwt-secret");
        DEFAULT_CONFIG.addProperty("api.endpoint", "http://your-api-endpoint.com");
    }

    private JsonObject configData;

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                configData = GSON.fromJson(reader, JsonObject.class);
                mergeDefaultConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            createDefaultConfig();
        }
    }

    private void mergeDefaultConfig() {
        for (Map.Entry<String, JsonElement> entry : DEFAULT_CONFIG.entrySet()) {
            if (!configData.has(entry.getKey())) {
                configData.add(entry.getKey(), entry.getValue());
            }
        }
    }

    private void createDefaultConfig() {
        configData = DEFAULT_CONFIG.deepCopy();
        saveConfig();
    }

    private void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
            System.out.println("Config file saved: " + CONFIG_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getJwtSecret() {
        return configData.get("jwt.secret").getAsString();
    }

    public String getApiEndpoint() {
        return configData.get("api.endpoint").getAsString();
    }
}