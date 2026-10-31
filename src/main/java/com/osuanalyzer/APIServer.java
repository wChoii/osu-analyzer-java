package com.osuanalyzer;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.URLDecoder;

public class APIServer {

    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/test", exchange -> {
            sendResponse(exchange, 200, "Hello from Java!");
        });

        server.createContext("/analyze", exchange -> {

            // Allow requests from the website
            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
            );

            if (!exchange.getRequestMethod().equals("GET")) {
                sendJson(exchange, 405,
                    errorResponse("Only GET requests are supported."));
                return;
            }

            try {
                String username = getQueryParameter(
                    exchange, "username"
                );

                if (username == null || username.isBlank()) {
                    sendJson(exchange, 400,
                        errorResponse("Missing username."));
                    return;
                }

                HttpClient client = HttpClient.newHttpClient();

                // Authenticate with osu!
                String accessToken = OsuApi.getAccessToken(client);

                // Retrieve player profile
                JsonObject userData = OsuApi.fetchUser(
                    client, accessToken, username
                );

                // Retrieve top 100 scores
                int userId = userData.get("id").getAsInt();

                JsonArray scores = OsuApi.fetchTopScores(
                    client, accessToken, userId
                );

                // Analyze scores
                ScoreAnalysis analysis =
                    ScoreAnalyzer.analyzeScores(scores, client, accessToken);

                // Build JSON response
                JsonObject result = new JsonObject();

                JsonObject statistics =
                    userData.getAsJsonObject("statistics");

                result.addProperty(
                    "username", userData.get("username").getAsString()
                );

                result.addProperty(
                    "pp", statistics.get("pp").getAsDouble()
                );

                if (!statistics.get("global_rank").isJsonNull()) {
                    result.addProperty(
                        "globalRank",
                        statistics.get("global_rank").getAsInt()
                    );
                } else {
                    result.add("globalRank", null);
                }

                result.addProperty(
                    "accuracy",
                    statistics.get("hit_accuracy").getAsDouble()
                );

                result.addProperty(
                    "averagePp", analysis.getAveragePp()
                );

                result.addProperty(
                    "averageAccuracy", analysis.getAverageAccuracy()
                );

                result.addProperty(
                    "highestPp", analysis.getHighestPp()
                );

                // Include individual scores
                JsonArray scoreResults = new JsonArray();

                for (ScoreData score : analysis.getScores()) {
                    JsonObject scoreObject = new JsonObject();

                    scoreObject.addProperty("pp", score.getPp());
                    scoreObject.addProperty(
                        "accuracy", score.getAccuracy()
                    );
                    scoreObject.addProperty(
                        "maxCombo", score.getMaxCombo()
                    );

                    scoreResults.add(scoreObject);
                }

                result.add("scores", scoreResults);

                sendJson(exchange, 200, gson.toJson(result));

            } catch (Exception e) {
                e.printStackTrace();

                sendJson(exchange, 500,
                    errorResponse("Unable to analyze player: "
                        + e.getMessage()));
            }
        });

        server.start();

        System.out.println(
            "Server running at http://localhost:8080"
        );
    }

    private static String getQueryParameter(
        HttpExchange exchange, String parameter
    ) {

        String query = exchange.getRequestURI().getRawQuery();

        if (query == null) {
            return null;
        }

        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                parts[0], StandardCharsets.UTF_8
            );

            if (key.equals(parameter)) {
                if (parts.length < 2) {
                    return "";
                }

                return URLDecoder.decode(
                    parts[1], StandardCharsets.UTF_8
                );
            }
        }

        return null;
    }

    private static String errorResponse(String message) {
        JsonObject error = new JsonObject();
        error.addProperty("error", message);
        return gson.toJson(error);
    }

    private static void sendJson(
        HttpExchange exchange, int statusCode, String response
    ) throws IOException {

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        sendResponse(exchange, statusCode, response);
    }

    private static void sendResponse(
        HttpExchange exchange, int statusCode, String response
    ) throws IOException {

        byte[] responseBytes =
            response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
            statusCode, responseBytes.length
        );

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(responseBytes);
        }
    }
}