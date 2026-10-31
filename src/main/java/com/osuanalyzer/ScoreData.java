package com.osuanalyzer;

public class ScoreData {
    private double pp;
    private double accuracy;
    private int maxCombo;

    public ScoreData(double pp, double accuracy, int maxCombo) {
        this.pp = pp;
        this.accuracy = accuracy;
        this.maxCombo = maxCombo;
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
}