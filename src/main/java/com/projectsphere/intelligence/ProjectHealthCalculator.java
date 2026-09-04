package com.projectsphere.intelligence;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Task;

import java.time.LocalDate;
import java.util.List;
import java.time.temporal.ChronoUnit;

public class ProjectHealthCalculator {

    public static final double COMMIT_TREND_WEIGHT = 0.40;
    public static final double TASK_COMPLETION_WEIGHT = 0.40;
    public static final double DEADLINE_WEIGHT = 0.20;

    public HealthResult calculate(Project project, List<Task> tasks, double commitTrend, double taskCompletionRate) {
        double deadlineScore = calculateDeadlineScore(project);
        double healthScore = (commitTrend * COMMIT_TREND_WEIGHT)
            + (taskCompletionRate * TASK_COMPLETION_WEIGHT)
            + (deadlineScore * DEADLINE_WEIGHT);

        String status = classify(healthScore);
        return new HealthResult(healthScore, commitTrend, taskCompletionRate, deadlineScore, status);
    }

    public double calculateDeadlineScore(Project project) {
        if (project == null || project.getDeadline() == null) {
            return 100;
        }
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), project.getDeadline());
        if (daysRemaining > 14) {
            return 100;
        }
        if (daysRemaining > 7) {
            return 75;
        }
        if (daysRemaining >= 0) {
            return 50;
        }
        return 0;
    }

    public String classify(double value) {
        if (value >= 80) return "HEALTHY";
        if (value >= 60) return "MODERATE";
        if (value >= 40) return "AT_RISK";
        return "CRITICAL";
    }

    public static class HealthResult {
        private final double healthScore;
        private final double commitTrend;
        private final double taskCompletionRate;
        private final double deadlineScore;
        private final String status;

        public HealthResult(double healthScore, double commitTrend, double taskCompletionRate, double deadlineScore, String status) {
            this.healthScore = healthScore;
            this.commitTrend = commitTrend;
            this.taskCompletionRate = taskCompletionRate;
            this.deadlineScore = deadlineScore;
            this.status = status;
        }

        public double getHealthScore() { return healthScore; }
        public double getCommitTrend() { return commitTrend; }
        public double getTaskCompletionRate() { return taskCompletionRate; }
        public double getDeadlineScore() { return deadlineScore; }
        public String getStatus() { return status; }
    }
}
