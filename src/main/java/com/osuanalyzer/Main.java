package com.osuanalyzer;
import java.util.Scanner;
import java.net.http.HttpClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class Main {
    public static void main(String[] args) throws Exception {
        
        HttpClient client = HttpClient.newHttpClient();
        String accessToken = OsuApi.getAccessToken(client);
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter osu! username: ");
        String user = scanner.nextLine();
        JsonObject userData;
        try {
            userData = OsuApi.fetchUser(client, accessToken, user);
        } catch (Exception e) {
            System.out.println("Error fetching user data: ");
            return;
        }


        int userId = userData.get("id").getAsInt();
        String username = userData.get("username").getAsString();
        JsonObject stats = userData.getAsJsonObject("statistics");

        JsonArray scores;
        try {
            scores = OsuApi.fetchTopScores(client, accessToken, userId);
        } catch (Exception e) {
            System.out.println("Error fetching top scores: " + e.getMessage());
            return;
        }

        //look at specific stats
        double pp = stats.get("pp").getAsDouble();
        //checck if global rank is null;
        if (stats.get("global_rank") == null || stats.get("global_rank").isJsonNull()) {
                System.out.println("Global Rank Is Currently Unavailable");
        }
        else {
            int globalRank = stats.get("global_rank").getAsInt();
            System.out.println("Global Rank: " + globalRank);   
        }
        double accuracy = stats.get("hit_accuracy").getAsDouble();

        System.out.println("Username: " + username);
        System.out.println("User ID: " + userId);
        System.out.println("PP: " + pp);
        System.out.println("Accuracy: " + accuracy + "%");
        
        System.out.println("Number of top scores fetched: " + scores.size());  

        ScoreAnalysis analysis = ScoreAnalyzer.analyzeScores(scores);

        System.out.println("Average PP: " + analysis.getAveragePp());
        System.out.println("Average Accuracy: " + analysis.getAverageAccuracy() + "%");
        System.out.println("Highest PP: " + analysis.getHighestPp());

        for (ScoreData score : analysis.getScores()) {
            System.out.println("PP: " + score.getPp() + " | Accuracy: " + score.getAccuracy() + "%" + " | Max Combo: " + score.getMaxCombo());
        }
    }
}