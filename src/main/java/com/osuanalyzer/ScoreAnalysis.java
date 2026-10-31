
package com.osuanalyzer;

import java.util.List;
import java.util.Map;

public class ScoreAnalysis {

    private double averagePp;
    private double averageAccuracy;
    private double highestPp;
    private double accuracyStandardDeviation;
    private List<ScoreData> scores;

    private double averageStars;
    private double highestStars;
    private double lowestStars;

    private int under5;
    private int fiveTo6;
    private int sixTo7;
    private int sevenTo8;
    private int eightPlus;
    private int starCount;

    private Map<String, Integer> modCounts;
    private Map<String, Double> averagePpByMod;
    private Map<String, Double> averageAccuracyByMod;
    private Map<String, Double> averageStarsByMod;

    private double ppStandardDeviation;
    private Map<String, Double> averageAccuracyByDifficulty;

    // Skill analysis
    private double averageAimDifficulty;
    private double averageSpeedDifficulty;
    private int aimScoreCount;
    private int speedScoreCount;

    private Map<String, Double> averageAccuracyByAimDifficulty;
    private Map<String, Double> averageAccuracyBySpeedDifficulty;

    // Number of scores contributing to each accuracy bucket
    private Map<String, Integer> accuracyCountsByAimDifficulty;
    private Map<String, Integer> accuracyCountsBySpeedDifficulty;

    // Data points for an aim-vs-speed scatter plot
    private List<SkillPoint> skillPoints;

    public static class SkillPoint {
        private double aimDifficulty;
        private double speedDifficulty;
        private double pp;
        private double accuracy;

        public SkillPoint(
                double aimDifficulty,
                double speedDifficulty,
                double pp,
                double accuracy) {
            this.aimDifficulty = aimDifficulty;
            this.speedDifficulty = speedDifficulty;
            this.pp = pp;
            this.accuracy = accuracy;
        }

        public double getAimDifficulty() {
            return aimDifficulty;
        }

        public double getSpeedDifficulty() {
            return speedDifficulty;
        }

        public double getPp() {
            return pp;
        }

        public double getAccuracy() {
            return accuracy;
        }
    }

    public ScoreAnalysis(
            double averagePp,
            double averageAccuracy,
            double highestPp,
            double accuracyStandardDeviation,
            List<ScoreData> scores,
            double averageStars,
            double highestStars,
            double lowestStars,
            int under5,
            int fiveTo6,
            int sixTo7,
            int sevenTo8,
            int eightPlus,
            int starCount,
            Map<String, Integer> modCounts,
            Map<String, Double> averagePpByMod,
            Map<String, Double> averageAccuracyByMod,
            Map<String, Double> averageStarsByMod,
            double ppStandardDeviation,
            Map<String, Double> averageAccuracyByDifficulty,
            double averageAimDifficulty,
            double averageSpeedDifficulty,
            int aimScoreCount,
            int speedScoreCount,
            Map<String, Double> averageAccuracyByAimDifficulty,
            Map<String, Double> averageAccuracyBySpeedDifficulty,
            Map<String, Integer> accuracyCountsByAimDifficulty,
            Map<String, Integer> accuracyCountsBySpeedDifficulty,
            List<SkillPoint> skillPoints
    ) {
        this.averagePp = averagePp;
        this.averageAccuracy = averageAccuracy;
        this.highestPp = highestPp;
        this.accuracyStandardDeviation = accuracyStandardDeviation;
        this.scores = scores;

        this.averageStars = averageStars;
        this.highestStars = highestStars;
        this.lowestStars = lowestStars;

        this.under5 = under5;
        this.fiveTo6 = fiveTo6;
        this.sixTo7 = sixTo7;
        this.sevenTo8 = sevenTo8;
        this.eightPlus = eightPlus;
        this.starCount = starCount;

        this.modCounts = modCounts;
        this.averagePpByMod = averagePpByMod;
        this.averageAccuracyByMod = averageAccuracyByMod;
        this.averageStarsByMod = averageStarsByMod;

        this.ppStandardDeviation = ppStandardDeviation;
        this.averageAccuracyByDifficulty = averageAccuracyByDifficulty;

        this.averageAimDifficulty = averageAimDifficulty;
        this.averageSpeedDifficulty = averageSpeedDifficulty;
        this.aimScoreCount = aimScoreCount;
        this.speedScoreCount = speedScoreCount;

        this.averageAccuracyByAimDifficulty =
                averageAccuracyByAimDifficulty;
        this.averageAccuracyBySpeedDifficulty =
                averageAccuracyBySpeedDifficulty;

        this.accuracyCountsByAimDifficulty =
                accuracyCountsByAimDifficulty;
        this.accuracyCountsBySpeedDifficulty =
                accuracyCountsBySpeedDifficulty;

        this.skillPoints = skillPoints;
    }

    public double getAveragePp() {
        return averagePp;
    }

    public double getAverageAccuracy() {
        return averageAccuracy;
    }

    public double getHighestPp() {
        return highestPp;
    }

    public double getAccuracyStandardDeviation() {
        return accuracyStandardDeviation;
    }

    public List<ScoreData> getScores() {
        return scores;
    }

    public double getAverageStars() {
        return averageStars;
    }

    public double getHighestStars() {
        return highestStars;
    }

    public double getLowestStars() {
        return lowestStars;
    }

    public int getUnder5() {
        return under5;
    }

    public int getFiveTo6() {
        return fiveTo6;
    }

    public int getSixTo7() {
        return sixTo7;
    }

    public int getSevenTo8() {
        return sevenTo8;
    }

    public int getEightPlus() {
        return eightPlus;
    }

    public int getStarCount() {
        return starCount;
    }

    public Map<String, Integer> getModCounts() {
        return modCounts;
    }

    public Map<String, Double> getAveragePpByMod() {
        return averagePpByMod;
    }

    public Map<String, Double> getAverageAccuracyByMod() {
        return averageAccuracyByMod;
    }

    public Map<String, Double> getAverageStarsByMod() {
        return averageStarsByMod;
    }

    public double getPpStandardDeviation() {
        return ppStandardDeviation;
    }

    public Map<String, Double> getAverageAccuracyByDifficulty() {
        return averageAccuracyByDifficulty;
    }

    public double getAverageAimDifficulty() {
        return averageAimDifficulty;
    }

    public double getAverageSpeedDifficulty() {
        return averageSpeedDifficulty;
    }

    public int getAimScoreCount() {
        return aimScoreCount;
    }

    public int getSpeedScoreCount() {
        return speedScoreCount;
    }

    public Map<String, Double> getAverageAccuracyByAimDifficulty() {
        return averageAccuracyByAimDifficulty;
    }

    public Map<String, Double> getAverageAccuracyBySpeedDifficulty() {
        return averageAccuracyBySpeedDifficulty;
    }

    public Map<String, Integer> getAccuracyCountsByAimDifficulty() {
        return accuracyCountsByAimDifficulty;
    }

    public Map<String, Integer> getAccuracyCountsBySpeedDifficulty() {
        return accuracyCountsBySpeedDifficulty;
    }

    public List<SkillPoint> getSkillPoints() {
        return skillPoints;
    }
}
