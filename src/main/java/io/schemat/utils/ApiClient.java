package io.schemat.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private static final Gson GSON = new Gson();
    private final String apiEndpoint;
    private final String jwtToken;

    public ApiClient(ConfigManager configManager) {
        this.apiEndpoint = configManager.getApiEndpoint();
        this.jwtToken = configManager.getJwtSecret();
    }

    public JsonElement makePostRequest(String path, JsonObject payload) throws IOException, InterruptedException {
        String url = apiEndpoint + path;
        String jsonPayload = GSON.toJson(payload);
        System.out.println("Making POST request to: " + url);
        System.out.println("Payload: " + jsonPayload);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + jwtToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 422) {

            JsonElement jsonElement = JsonParser.parseString(response.body());
            if (jsonElement.isJsonObject()) {
                return jsonElement.getAsJsonObject();
            } else {
                return jsonElement;
            }
        }
        int statusCode = response.statusCode();

        throw new IOException("status code: " + statusCode);
    }
}