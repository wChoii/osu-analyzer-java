
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
            System.out.println("Error fetching user data: " + e.getMessage());
            scanner.close();
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
            scanner.close();
            return;
        }

        // Profile information
        double pp = stats.get("pp").getAsDouble();
        double accuracy = stats.get("hit_accuracy").getAsDouble();

        System.out.println("\n========== PLAYER PROFILE ==========");
        System.out.println("Username: " + username);
        System.out.println("User ID: " + userId);
        System.out.printf("PP: %.2f%n", pp);
        System.out.printf("Accuracy: %.2f%%%n", accuracy);

        if (stats.has("global_rank") && !stats.get("global_rank").isJsonNull()) {
            System.out.println("Global Rank: " + stats.get("global_rank").getAsInt());
        } else {
            System.out.println("Global Rank: Unavailable");
        }

        System.out.println("Scores analyzed: " + scores.size());

        // Analyze scores
        ScoreAnalysis analysis =
                ScoreAnalyzer.analyzeScores(scores, client, accessToken);

        // Overall statistics
        System.out.println("\n========== OVERALL ANALYSIS ==========");

        System.out.printf("Average PP: %.2f%n", analysis.getAveragePp());
        System.out.printf("Average Accuracy: %.2f%%%n", analysis.getAverageAccuracy());
        System.out.printf("Highest PP: %.2f%n", analysis.getHighestPp());

        // Difficulty analysis
        System.out.println("\n========== DIFFICULTY ANALYSIS ==========");

        System.out.printf("Average Stars: %.2f★%n", analysis.getAverageStars());
        System.out.printf("Highest Stars: %.2f★%n", analysis.getHighestStars());
        System.out.printf("Lowest Stars: %.2f★%n", analysis.getLowestStars());

        System.out.println("\nDifficulty Distribution:");

        System.out.println("Under 5★: " + analysis.getUnder5());
        System.out.println("5–6★: " + analysis.getFiveTo6());
        System.out.println("6–7★: " + analysis.getSixTo7());
        System.out.println("7–8★: " + analysis.getSevenTo8());
        System.out.println("8★+: " + analysis.getEightPlus());

        System.out.println("Scores with difficulty data: " + analysis.getStarCount());

        // Mod analysis
        System.out.println("\n========== MOD ANALYSIS ==========");

        analysis.getModCounts().entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry ->
                        System.out.println(
                                entry.getKey() + ": " + entry.getValue() + " scores"));

        // Mod performance
        System.out.println("\n========== MOD PERFORMANCE ==========");

        for (String mod : analysis.getModCounts().keySet()) {

            System.out.println("\n" + mod + ":");

            System.out.printf(
                    "  Scores: %d%n",
                    analysis.getModCounts().get(mod));

            System.out.printf(
                    "  Average PP: %.2f%n",
                    analysis.getAveragePpByMod().getOrDefault(mod, 0.0));

            System.out.printf(
                    "  Average Accuracy: %.2f%%%n",
                    analysis.getAverageAccuracyByMod().getOrDefault(mod, 0.0));

            System.out.printf(
                    "  Average Difficulty: %.2f★%n",
                    analysis.getAverageStarsByMod().getOrDefault(mod, 0.0));
        }

        // Score consistency
        System.out.println("\n========== SCORE CONSISTENCY ==========");

        System.out.printf(
                "PP Standard Deviation: %.2f%n",
                analysis.getPpStandardDeviation());

        System.out.printf(
                "Accuracy Standard Deviation: %.2f percentage points%n",
                analysis.getAccuracyStandardDeviation());

        // Accuracy by difficulty
        System.out.println("\n========== ACCURACY BY DIFFICULTY ==========");

        analysis.getAverageAccuracyByDifficulty().forEach((range, avgAccuracy) ->
                System.out.printf("%s: %.2f%%%n", range, avgAccuracy));

        // Skill analysis
        System.out.println("\n========== SKILL ANALYSIS ==========");

        System.out.printf(
                "Average Aim Difficulty: %.2f%n",
                analysis.getAverageAimDifficulty());

        System.out.printf(
                "Average Speed Difficulty: %.2f%n",
                analysis.getAverageSpeedDifficulty());

        System.out.println(
                "Scores with aim data: " + analysis.getAimScoreCount());

        System.out.println(
                "Scores with speed data: " + analysis.getSpeedScoreCount());

        // Compare aim and speed
        System.out.println("\nAim vs. Speed:");

        double difference = analysis.getAverageAimDifficulty()
                - analysis.getAverageSpeedDifficulty();

        if (difference > 0.1) {
            System.out.println("Your top scores lean more toward aim difficulty.");
        } else if (difference < -0.1) {
            System.out.println("Your top scores lean more toward speed difficulty.");
        } else {
            System.out.println("Your top scores have relatively balanced aim and speed difficulty.");
        }

        // Accuracy by aim difficulty, including sample counts
        System.out.println("\n========== ACCURACY BY AIM DIFFICULTY ==========");

        analysis.getAverageAccuracyByAimDifficulty().entrySet().stream()
                .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
                .forEach(entry -> {

                    String range = entry.getKey();
                    double average = entry.getValue();

                    int count = analysis.getAccuracyCountsByAimDifficulty()
                            .getOrDefault(range, 0);

                    System.out.printf(
                            "%s aim: %.2f%% accuracy (%d scores)%n",
                            range, average, count);
                });

        // Accuracy by speed difficulty, including sample counts
        System.out.println("\n========== ACCURACY BY SPEED DIFFICULTY ==========");

        analysis.getAverageAccuracyBySpeedDifficulty().entrySet().stream()
                .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
                .forEach(entry -> {

                    String range = entry.getKey();
                    double average = entry.getValue();

                    int count = analysis.getAccuracyCountsBySpeedDifficulty()
                            .getOrDefault(range, 0);

                    System.out.printf(
                            "%s speed: %.2f%% accuracy (%d scores)%n",
                            range, average, count);
                });

        // Skill data retrieval check
        System.out.println("\n========== SKILL DATA RETRIEVAL ==========");

        long aimAvailable = analysis.getScores().stream()
                .filter(score -> score.getAimDifficulty() > 0)
                .count();

        long speedAvailable = analysis.getScores().stream()
                .filter(score -> score.getSpeedDifficulty() > 0)
                .count();

        System.out.println("Scores with aim data: " + aimAvailable);
        System.out.println("Scores with speed data: " + speedAvailable);

        // Aim vs. speed scatter plot data
        System.out.println("\n========== AIM VS SPEED DATA ==========");

        System.out.println(
                "Scatter plot points: " + analysis.getSkillPoints().size());

        scanner.close();
    }
}
