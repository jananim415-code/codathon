package com.projectsphere.intelligence;

import com.projectsphere.entity.Contribution;
import com.projectsphere.entity.Project;
import com.projectsphere.entity.Task;
import com.projectsphere.entity.User;

import java.util.List;

public class ContributionScorer {

    public static final double COMMIT_WEIGHT = 30.0;
    public static final double CHURN_WEIGHT = 25.0;
    public static final double FILES_WEIGHT = 15.0;
    public static final double PR_WEIGHT = 20.0;
    public static final double TASK_WEIGHT = 10.0;

    public ContributionScoreResult scoreUser(User user, List<Contribution> contributions, List<Task> tasks, Project project) {
        int commitCount = contributions.stream().mapToInt(Contribution::getCommitCount).sum();
        long codeChurn = contributions.stream().mapToLong(Contribution::getCodeChurn).sum();
        int filesChanged = contributions.stream().mapToInt(Contribution::getFilesChanged).sum();
        int pullRequestCount = contributions.stream().mapToInt(Contribution::getPullRequestCount).sum();

        long totalChurn = contributions.stream().mapToLong(Contribution::getCodeChurn).sum();
        long trivialChanges = contributions.stream().mapToLong(Contribution::getTrivialChanges).sum();
        long effectiveChurn = Math.max(0, totalChurn - trivialChanges);

        double taskCompletionRate = calculateTaskCompletionRate(user, tasks);
        double maxCommit = Math.max(1, contributions.stream().mapToInt(Contribution::getCommitCount).max().orElse(0));
        double maxChurn = Math.max(1, contributions.stream().mapToLong(Contribution::getCodeChurn).max().orElse(0));
        double maxFiles = Math.max(1, contributions.stream().mapToInt(Contribution::getFilesChanged).max().orElse(0));
        double maxPullRequests = Math.max(1, contributions.stream().mapToInt(Contribution::getPullRequestCount).max().orElse(0));

        double commitScore = normalize(commitCount, maxCommit) * COMMIT_WEIGHT;
        double churnScore = normalize(effectiveChurn, maxChurn) * CHURN_WEIGHT;
        double filesScore = normalize(filesChanged, maxFiles) * FILES_WEIGHT;
        double pullRequestScore = normalize(pullRequestCount, maxPullRequests) * PR_WEIGHT;
        double taskScore = taskCompletionRate * TASK_WEIGHT / 100.0;

        double total = safeCap(commitScore + churnScore + filesScore + pullRequestScore + taskScore);

        String explanation = buildExplanation(total, taskCompletionRate, trivialChanges);
        return new ContributionScoreResult(user, total, commitScore, churnScore, filesScore, pullRequestScore, taskScore, explanation);
    }

    private double calculateTaskCompletionRate(User user, List<Task> tasks) {
        List<Task> assigned = tasks.stream()
            .filter(task -> task.getAssignedUser() != null && user != null && user.getId() != null)
            .filter(task -> task.getAssignedUser().getId() != null && task.getAssignedUser().getId().equals(user.getId()))
            .toList();
        if (assigned.isEmpty()) {
            return 0;
        }
        long completed = assigned.stream().filter(task -> task.getStatus() == Task.Status.COMPLETED).count();
        return (completed * 100.0) / assigned.size();
    }

    private double normalize(double value, double maxValue) {
        if (maxValue <= 0) {
            return 0;
        }
        return Math.min(1.0, value / maxValue) * 100.0;
    }

    private double safeCap(double value) {
        return Math.min(100.0, Math.max(0.0, value));
    }

    private String buildExplanation(double total, double taskCompletionRate, long trivialChanges) {
        StringBuilder explanation = new StringBuilder();
        explanation.append("Contribution score: ").append(String.format("%.0f", total)).append("/100.");
        if (taskCompletionRate >= 75) {
            explanation.append(" Strong task completion and delivery.");
        } else if (taskCompletionRate >= 40) {
            explanation.append(" Moderate task completion.");
        } else {
            explanation.append(" Task completion is low.");
        }
        if (trivialChanges > 0) {
            explanation.append(" Minor deduction applied for trivial changes.");
        }
        return explanation.toString();
    }

    public static class ContributionScoreResult {
        private final User user;
        private final double score;
        private final double commitScore;
        private final double churnScore;
        private final double filesScore;
        private final double pullRequestScore;
        private final double taskScore;
        private final String explanation;

        public ContributionScoreResult(User user, double score, double commitScore, double churnScore, double filesScore, double pullRequestScore, double taskScore, String explanation) {
            this.user = user;
            this.score = score;
            this.commitScore = commitScore;
            this.churnScore = churnScore;
            this.filesScore = filesScore;
            this.pullRequestScore = pullRequestScore;
            this.taskScore = taskScore;
            this.explanation = explanation;
        }

        public User getUser() { return user; }
        public double getScore() { return score; }
        public double getCommitScore() { return commitScore; }
        public double getChurnScore() { return churnScore; }
        public double getFilesScore() { return filesScore; }
        public double getPullRequestScore() { return pullRequestScore; }
        public double getTaskScore() { return taskScore; }
        public String getExplanation() { return explanation; }
    }
}
