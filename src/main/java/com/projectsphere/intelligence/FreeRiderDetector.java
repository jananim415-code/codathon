package com.projectsphere.intelligence;

import com.projectsphere.entity.User;

import java.util.ArrayList;
import java.util.List;

public class FreeRiderDetector {

    public static final double FREE_RIDER_Z_THRESHOLD = -1.0;

    public List<FreeRiderResult> detect(List<ContributionScorer.ContributionScoreResult> scores) {
        List<FreeRiderResult> results = new ArrayList<>();
        if (scores == null || scores.isEmpty()) {
            return results;
        }

        double mean = scores.stream().mapToDouble(ContributionScorer.ContributionScoreResult::getScore).average().orElse(0.0);
        double variance = scores.stream()
            .mapToDouble(result -> Math.pow(result.getScore() - mean, 2))
            .average()
            .orElse(0.0);
        double standardDeviation = Math.sqrt(variance);

        for (ContributionScorer.ContributionScoreResult result : scores) {
            boolean flagged = false;
            double zScore = 0.0;
            String explanation = "Normal contribution level within team range.";
            if (standardDeviation > 0) {
                zScore = (result.getScore() - mean) / standardDeviation;
                flagged = zScore <= FREE_RIDER_Z_THRESHOLD;
                if (flagged) {
                    explanation = "Contribution score " + String.format("%.0f", result.getScore()) + " is significantly below the team average of " + String.format("%.0f", mean) + ". Potential low contribution detected.";
                }
            }
            results.add(new FreeRiderResult(result.getUser(), result.getScore(), mean, zScore, flagged, explanation));
        }
        return results;
    }

    public static class FreeRiderResult {
        private final User user;
        private final double contributionScore;
        private final double teamAverage;
        private final double zScore;
        private final boolean flagged;
        private final String explanation;

        public FreeRiderResult(User user, double contributionScore, double teamAverage, double zScore, boolean flagged, String explanation) {
            this.user = user;
            this.contributionScore = contributionScore;
            this.teamAverage = teamAverage;
            this.zScore = zScore;
            this.flagged = flagged;
            this.explanation = explanation;
        }

        public User getUser() { return user; }
        public double getContributionScore() { return contributionScore; }
        public double getTeamAverage() { return teamAverage; }
        public double getZScore() { return zScore; }
        public boolean isFlagged() { return flagged; }
        public String getExplanation() { return explanation; }
    }
}
