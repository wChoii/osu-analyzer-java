
package com.osuanalyzer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreAnalyzer {

    private static String getDifficultyRange(double value) {
        if (value < 2) return "Under 2";
        if (value < 4) return "2–4";
        if (value < 6) return "4–6";
        if (value < 8) return "6–8";
        return "8+";
    }

    public static ScoreAnalysis analyzeScores(
            JsonArray scores,
            HttpClient client,
            String accessToken) throws Exception {

        Map<String, Double> totalPpByMod = new HashMap<>();
        Map<String, Double> totalAccuracyByMod = new HashMap<>();
        Map<String, Double> totalStarsByMod = new HashMap<>();

        Map<String, Integer> ppCountByMod = new HashMap<>();
        Map<String, Integer> accuracyCountByMod = new HashMap<>();
        Map<String, Integer> starCountByMod = new HashMap<>();

        double totalPp = 0;
        double totalAccuracy = 0;
        double highestPp = 0;

        double totalStars = 0;
        double highestStars = 0;
        double lowestStars = Double.MAX_VALUE;

        int ppCount = 0;
        int accuracyCount = 0;
        int starCount = 0;

        int under5 = 0;
        int fiveTo6 = 0;
        int sixTo7 = 0;
        int sevenTo8 = 0;
        int eightPlus = 0;

        List<Double> ppValues = new ArrayList<>();
        List<Double> accuracyValues = new ArrayList<>();

        Map<String, Double> accuracyTotalsByDifficulty = new HashMap<>();
        Map<String, Integer> accuracyCountsByDifficulty = new HashMap<>();

        // Skill analysis totals
        double totalAimDifficulty = 0;
        double totalSpeedDifficulty = 0;

        int aimScoreCount = 0;
        int speedScoreCount = 0;

        Map<String, Double> accuracyTotalsByAim = new HashMap<>();
        Map<String, Integer> accuracyCountsByAim = new HashMap<>();

        Map<String, Double> accuracyTotalsBySpeed = new HashMap<>();
        Map<String, Integer> accuracyCountsBySpeed = new HashMap<>();

        // Cache attributes by beatmap ID
        Map<Integer, double[]> attributeCache = new HashMap<>();

        List<ScoreData> scoreDataList = new ArrayList<>();
        Map<String, Integer> modCounts = new HashMap<>();
        List<ScoreAnalysis.SkillPoint> skillPoints = new ArrayList<>();

        if (scores == null || scores.isEmpty()) {
            return new ScoreAnalysis(
                    0, 0, 0, 0, scoreDataList,
                    0, 0, 0, 0, 0, 0, 0, 0, 0,
                    modCounts,
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    0,
                    new HashMap<>(),
                    0, 0, 0, 0,
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new ArrayList<>()
            );
        }

        for (JsonElement scoreElement : scores) {

            if (scoreElement == null || scoreElement.isJsonNull()) {
                continue;
            }

            JsonObject score = scoreElement.getAsJsonObject();

            double scoreAccuracy =
                    score.has("accuracy") && !score.get("accuracy").isJsonNull()
                    ? score.get("accuracy").getAsDouble() * 100 : 0;

            double scorePp =
                    score.has("pp") && !score.get("pp").isJsonNull()
                    ? score.get("pp").getAsDouble() : 0;

            int maxCombo =
                    score.has("max_combo") && !score.get("max_combo").isJsonNull()
                    ? score.get("max_combo").getAsInt() : 0;

            double starRating = 0;
            double aimDifficulty = 0;
            double speedDifficulty = 0;
            int beatmapId = -1;

            // Read beatmap information
            if (score.has("beatmap") && score.get("beatmap").isJsonObject()) {

                JsonObject beatmap = score.getAsJsonObject("beatmap");

                if (beatmap.has("id") && !beatmap.get("id").isJsonNull()) {
                    beatmapId = beatmap.get("id").getAsInt();
                }

                if (beatmap.has("difficulty_rating")
                        && !beatmap.get("difficulty_rating").isJsonNull()) {
                    starRating =
                            beatmap.get("difficulty_rating").getAsDouble();
                }
            }

            // Retrieve and cache beatmap attributes
            if (beatmapId != -1) {

                if (!attributeCache.containsKey(beatmapId)) {

                    double[] attributes = {0, 0};

                    try {
                        JsonObject response =
                                OsuApi.fetchBeatmapAttributes(
                                        client, accessToken, beatmapId);

                        if (response.has("attributes")
                                && response.get("attributes").isJsonObject()) {

                            JsonObject data =
                                    response.getAsJsonObject("attributes");

                            if (data.has("aim_difficulty")
                                    && !data.get("aim_difficulty").isJsonNull()) {
                                attributes[0] =
                                        data.get("aim_difficulty").getAsDouble();
                            }

                            if (data.has("speed_difficulty")
                                    && !data.get("speed_difficulty").isJsonNull()) {
                                attributes[1] =
                                        data.get("speed_difficulty").getAsDouble();
                            }
                        }

                    } catch (Exception e) {
                        System.err.println(
                                "Could not retrieve attributes for beatmap "
                                        + beatmapId + ": " + e.getMessage());
                    }

                    attributeCache.put(beatmapId, attributes);
                }

                double[] attributes = attributeCache.get(beatmapId);
                aimDifficulty = attributes[0];
                speedDifficulty = attributes[1];
            }

            // Extract mods
            List<String> mods = new ArrayList<>();

            if (score.has("mods") && score.get("mods").isJsonArray()) {
                JsonArray modsArray = score.getAsJsonArray("mods");

                for (JsonElement mod : modsArray) {
                    if (!mod.isJsonNull()) {
                        mods.add(mod.getAsString());
                    }
                }
            }

            List<String> modsToAnalyze =
                    mods.isEmpty() ? List.of("NM") : mods;

            // Mod analysis
            for (String mod : modsToAnalyze) {

                modCounts.merge(mod, 1, Integer::sum);

                if (score.has("pp") && !score.get("pp").isJsonNull()) {
                    totalPpByMod.merge(mod, scorePp, Double::sum);
                    ppCountByMod.merge(mod, 1, Integer::sum);
                }

                if (score.has("accuracy") && !score.get("accuracy").isJsonNull()) {
                    totalAccuracyByMod.merge(mod, scoreAccuracy, Double::sum);
                    accuracyCountByMod.merge(mod, 1, Integer::sum);
                }

                if (starRating > 0) {
                    totalStarsByMod.merge(mod, starRating, Double::sum);
                    starCountByMod.merge(mod, 1, Integer::sum);
                }
            }

            // Overall PP
            if (score.has("pp") && !score.get("pp").isJsonNull()) {
                totalPp += scorePp;
                ppCount++;
                highestPp = Math.max(highestPp, scorePp);
                ppValues.add(scorePp);
            }

            // Overall accuracy
            if (score.has("accuracy") && !score.get("accuracy").isJsonNull()) {
                totalAccuracy += scoreAccuracy;
                accuracyCount++;
                accuracyValues.add(scoreAccuracy);
            }

            // Star rating analysis
            if (starRating > 0) {

                totalStars += starRating;
                starCount++;

                highestStars = Math.max(highestStars, starRating);
                lowestStars = Math.min(lowestStars, starRating);

                String range;

                if (starRating < 5) {
                    under5++;
                    range = "Under 5★";
                } else if (starRating < 6) {
                    fiveTo6++;
                    range = "5–6★";
                } else if (starRating < 7) {
                    sixTo7++;
                    range = "6–7★";
                } else if (starRating < 8) {
                    sevenTo8++;
                    range = "7–8★";
                } else {
                    eightPlus++;
                    range = "8★+";
                }

                if (score.has("accuracy")
                        && !score.get("accuracy").isJsonNull()) {

                    accuracyTotalsByDifficulty.merge(
                            range, scoreAccuracy, Double::sum);

                    accuracyCountsByDifficulty.merge(
                            range, 1, Integer::sum);
                }
            }

            // Aim and speed analysis
            if (aimDifficulty > 0) {

                totalAimDifficulty += aimDifficulty;
                aimScoreCount++;

                if (score.has("accuracy")
                        && !score.get("accuracy").isJsonNull()) {

                    String range = getDifficultyRange(aimDifficulty);

                    accuracyTotalsByAim.merge(
                            range, scoreAccuracy, Double::sum);

                    accuracyCountsByAim.merge(
                            range, 1, Integer::sum);
                }
            }

            if (speedDifficulty > 0) {

                totalSpeedDifficulty += speedDifficulty;
                speedScoreCount++;

                if (score.has("accuracy")
                        && !score.get("accuracy").isJsonNull()) {

                    String range = getDifficultyRange(speedDifficulty);

                    accuracyTotalsBySpeed.merge(
                            range, scoreAccuracy, Double::sum);

                    accuracyCountsBySpeed.merge(
                            range, 1, Integer::sum);
                }
            }

            // Store valid aim/speed pairs for the scatter plot
            if (aimDifficulty > 0 && speedDifficulty > 0) {
                skillPoints.add(
                        new ScoreAnalysis.SkillPoint(
                                aimDifficulty,
                                speedDifficulty,
                                scorePp,
                                scoreAccuracy
                        )
                );
            }

            // Store score data
            scoreDataList.add(
                    new ScoreData(
                            scorePp,
                            scoreAccuracy,
                            maxCombo,
                            starRating,
                            mods,
                            aimDifficulty,
                            speedDifficulty
                    )
            );
        }

        // Overall averages
        double averagePp = ppCount > 0 ? totalPp / ppCount : 0;

        double averageAccuracy =
                accuracyCount > 0 ? totalAccuracy / accuracyCount : 0;

        double averageStars =
                starCount > 0 ? totalStars / starCount : 0;

        if (starCount == 0) {
            lowestStars = 0;
        }

        // Accuracy standard deviation
        double accuracySquaredDifferences = 0;

        for (double accuracy : accuracyValues) {
            accuracySquaredDifferences +=
                    Math.pow(accuracy - averageAccuracy, 2);
        }

        double accuracyVariance = accuracyCount > 0
                ? accuracySquaredDifferences / accuracyCount : 0;

        double accuracyStandardDeviation =
                Math.sqrt(accuracyVariance);

        // PP standard deviation
        double ppSquaredDifferences = 0;

        for (double pp : ppValues) {
            ppSquaredDifferences += Math.pow(pp - averagePp, 2);
        }

        double ppVariance = ppCount > 0
                ? ppSquaredDifferences / ppCount : 0;

        double ppStandardDeviation = Math.sqrt(ppVariance);

        // Mod averages
        Map<String, Double> averagePpByMod = new HashMap<>();
        Map<String, Double> averageAccuracyByMod = new HashMap<>();
        Map<String, Double> averageStarsByMod = new HashMap<>();

        for (String mod : totalPpByMod.keySet()) {
            averagePpByMod.put(
                    mod, totalPpByMod.get(mod) / ppCountByMod.get(mod));
        }

        for (String mod : totalAccuracyByMod.keySet()) {
            averageAccuracyByMod.put(
                    mod, totalAccuracyByMod.get(mod)
                            / accuracyCountByMod.get(mod));
        }

        for (String mod : totalStarsByMod.keySet()) {
            averageStarsByMod.put(
                    mod, totalStarsByMod.get(mod)
                            / starCountByMod.get(mod));
        }

        // Accuracy by star difficulty
        Map<String, Double> averageAccuracyByDifficulty = new HashMap<>();

        for (String range : accuracyTotalsByDifficulty.keySet()) {
            averageAccuracyByDifficulty.put(
                    range,
                    accuracyTotalsByDifficulty.get(range)
                            / accuracyCountsByDifficulty.get(range));
        }

        // Accuracy by aim difficulty
        Map<String, Double> averageAccuracyByAimDifficulty =
                new HashMap<>();

        for (String range : accuracyTotalsByAim.keySet()) {
            averageAccuracyByAimDifficulty.put(
                    range,
                    accuracyTotalsByAim.get(range)
                            / accuracyCountsByAim.get(range));
        }

        // Accuracy by speed difficulty
        Map<String, Double> averageAccuracyBySpeedDifficulty =
                new HashMap<>();

        for (String range : accuracyTotalsBySpeed.keySet()) {
            averageAccuracyBySpeedDifficulty.put(
                    range,
                    accuracyTotalsBySpeed.get(range)
                            / accuracyCountsBySpeed.get(range));
        }

        double averageAimDifficulty = aimScoreCount > 0
                ? totalAimDifficulty / aimScoreCount : 0;

        double averageSpeedDifficulty = speedScoreCount > 0
                ? totalSpeedDifficulty / speedScoreCount : 0;

        return new ScoreAnalysis(
                averagePp,
                averageAccuracy,
                highestPp,
                accuracyStandardDeviation,
                scoreDataList,
                averageStars,
                highestStars,
                lowestStars,
                under5,
                fiveTo6,
                sixTo7,
                sevenTo8,
                eightPlus,
                starCount,
                modCounts,
                averagePpByMod,
                averageAccuracyByMod,
                averageStarsByMod,
                ppStandardDeviation,
                averageAccuracyByDifficulty,
                averageAimDifficulty,
                averageSpeedDifficulty,
                aimScoreCount,
                speedScoreCount,
                averageAccuracyByAimDifficulty,
                averageAccuracyBySpeedDifficulty,
                accuracyCountsByAim,
                accuracyCountsBySpeed,
                skillPoints
        );
    }
}
