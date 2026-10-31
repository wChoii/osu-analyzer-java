package com.osuanalyzer;

import java.util.List;

public class ScoreAnalysis {
    private double averagePp;
    private double averageAccuracy;
    private List<ScoreData> scores;
    private double highestPp;

    public  ScoreAnalysis (double averagePp, double averageAccuracy, double highestPp, List<ScoreData> scores) {
        this.averagePp = averagePp;
        this.averageAccuracy = averageAccuracy;
        this.scores = scores;
        this.highestPp = highestPp;
    }

    public double getAveragePp() {
        return averagePp;
    }
    public double getAverageAccuracy() {
        return averageAccuracy;
    }
    public List<ScoreData> getScores() {
        return scores;
    }
    public double getHighestPp() {
        return highestPp;
    }
}
