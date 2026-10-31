package com.osuanalyzer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;


public class OsuApi {
    private static String getEnvValue(String key) throws IOException {
        for (String line : java.nio.file.Files.readAllLines(java.nio.file.Path.of(".env"))) {
            if (line.startsWith(key + "=")) {
                return line.substring(key.length() + 1);
            }
        }
        throw new IOException("Missing " + key + " in .env");
    }

    public static String getAccessToken(HttpClient client) throws Exception {
        String clientId = getEnvValue("OSU_CLIENT_ID");
        String clientSecret = getEnvValue("OSU_CLIENT_SECRET");
        
        String body = "client_id=" + clientId
            + "&client_secret=" + clientSecret
            + "&grant_type=client_credentials"
            + "&scope=public";

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
        return accessToken;
        }

        public static JsonObject fetchUser(HttpClient client, String accessToken, String username) throws Exception {
            HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://osu.ppy.sh/api/v2/users/" + username + "/osu"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .GET()
                .build();

            HttpResponse<String> userResponse = client.send(userRequest, HttpResponse.BodyHandlers.ofString());

            if (userResponse.statusCode() != 200) {
                throw new IOException("User request failed: " + userResponse.statusCode());
            }
            return JsonParser.parseString(userResponse.body()).getAsJsonObject();
        }

        public static JsonArray fetchTopScores(HttpClient client, String accessToken, int userId) throws Exception {
            HttpRequest scoresRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://osu.ppy.sh/api/v2/users/" + userId + "/scores/best?mode=osu&limit=100"))
            .header("Authorization", "Bearer " + accessToken)
            .header("Accept", "application/json")
            .GET()
            .build();

            //score results
            HttpResponse<String> scoresResponse = client.send(scoresRequest, HttpResponse.BodyHandlers.ofString());
            if (scoresResponse.statusCode() != 200) {
                throw new IOException("Scores request failed: " + scoresResponse.statusCode() + " | " + scoresResponse.body());
            }

            return JsonParser.parseString(scoresResponse.body()).getAsJsonArray();
        }
}
