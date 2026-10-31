
package com.osuanalyzer;

import java.util.List;

public class ScoreData {

    private double pp;
    private double accuracy;
    private int maxCombo;
    private double starRating;
    private List<String> mods;

    private double aimDifficulty;
    private double speedDifficulty;

    public ScoreData(
            double pp,
            double accuracy,
            int maxCombo,
            double starRating,
            List<String> mods,
            double aimDifficulty,
            double speedDifficulty) {

        this.pp = pp;
        this.accuracy = accuracy;
        this.maxCombo = maxCombo;
        this.starRating = starRating;
        this.mods = mods;
        this.aimDifficulty = aimDifficulty;
        this.speedDifficulty = speedDifficulty;
    }

    public double getPp() {
        return pp;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public double getStarRating() {
        return starRating;
    }

    public List<String> getMods() {
        return mods;
    }

    public double getAimDifficulty() {
        return aimDifficulty;
    }

    public double getSpeedDifficulty() {
        return speedDifficulty;
    }
}
