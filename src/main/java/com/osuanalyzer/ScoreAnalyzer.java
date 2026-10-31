package com.osuanalyzer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ScoreAnalyzer {
    public static ScoreAnalysis analyzeScores(JsonArray scores) {
        
        double totalPp = 0;
        double totalAccuracy = 0;
        double highestPp = 0;

        int ppCount = 0;
        int accuracyCount = 0;

        List<ScoreData> scoreDataList = new ArrayList<>();
        if (scores == null || scores.isEmpty()) {
            return new ScoreAnalysis(0, 0, 0, new ArrayList<>());
        }

        for (JsonElement scoreElement : scores) {
            if (scoreElement == null || scoreElement.isJsonNull()) {
                continue;
            }
            JsonObject score = scoreElement.getAsJsonObject();

            double scoreAccuracy = score.has("accuracy") && !score.get("accuracy").isJsonNull()
            ? score.get("accuracy").getAsDouble() * 100
            : 0;
            double scorePp = score.has("pp") && !score.get("pp").isJsonNull()?
            score.get("pp").getAsDouble(): 0;

            int maxCombo = score.get("max_combo").getAsInt();

            if (score.has("accuracy") && !score.get("accuracy").isJsonNull()) {
                totalAccuracy += score.get("accuracy").getAsDouble() * 100;
                accuracyCount++;
            }

            if (score.has("pp") && !score.get("pp").isJsonNull()) {
                totalPp += score.get("pp").getAsDouble();
                ppCount++;

                if (scorePp > highestPp) {
                    highestPp = scorePp;
            }
            }
            ScoreData scoreData = new ScoreData(scorePp, scoreAccuracy, maxCombo);
            scoreDataList.add(scoreData);
        }
        double averagePp = ppCount > 0 ? totalPp / ppCount : 0;
        double averageAccuracy = accuracyCount>0 ? totalAccuracy / accuracyCount : 0;

        return new ScoreAnalysis(averagePp, averageAccuracy, highestPp, scoreDataList);
    }
}
