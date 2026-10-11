package com.osuanalyzer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
//parse OAuth with Gson
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

    public static void main(String[] args) throws Exception {
        String clientId = getEnvValue("OSU_CLIENT_ID");
        String clientSecret = getEnvValue("OSU_CLIENT_SECRET");
        String body = "client_id=" + clientId
            + "&client_secret=" + clientSecret
            + "&grant_type=client_credentials"
            + "&scope=public";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://osu.ppy.sh/oauth/token"))
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpResponse<String> response =
            client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Token request failed: " + response.statusCode());
        }
        JsonObject tokenData = JsonParser.parseString(response.body()).getAsJsonObject();

        String accessToken = tokenData.get("access_token").getAsString();
        
        HttpRequest userRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://osu.ppy.sh/api/v2/users/peppy/osu"))
            .header("Authorization", "Bearer " + accessToken)
            .header("Accept", "application/json")
            .GET()
            .build();

            HttpResponse<String> userResponse = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(userResponse.body());
    }

    private static String getEnvValue(String key) throws IOException {
        for (String line : java.nio.file.Files.readAllLines(java.nio.file.Path.of(".env"))) {
            if (line.startsWith(key + "=")) {
                return line.substring(key.length() + 1);
            }
        }
        throw new IOException("Missing " + key + " in .env");
    }

}