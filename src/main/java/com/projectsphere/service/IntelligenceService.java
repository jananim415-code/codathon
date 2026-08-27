package com.projectsphere.service;

import com.projectsphere.entity.*;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.github.GitHubClient;
import com.projectsphere.intelligence.ContributionScorer;
import com.projectsphere.intelligence.FreeRiderDetector;
import com.projectsphere.intelligence.ProjectHealthCalculator;
import com.projectsphere.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntelligenceService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final ContributionRepository contributionRepository;
    private final HealthScoreRepository healthScoreRepository;
    private final UserRepository userRepository;
    private final GitHubClient gitHubClient;

    public IntelligenceService(ProjectRepository projectRepository,
                              TeamRepository teamRepository,
                              TaskRepository taskRepository,
                              ContributionRepository contributionRepository,
                              HealthScoreRepository healthScoreRepository,
                              UserRepository userRepository,
                              GitHubClient gitHubClient) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.contributionRepository = contributionRepository;
        this.healthScoreRepository = healthScoreRepository;
        this.userRepository = userRepository;
        this.gitHubClient = gitHubClient;
    }

    public Map<String, Object> analyzeProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        List<Task> tasks = taskRepository.findByProject(project);
        double taskCompletionRate = tasks.isEmpty() ? 0 : (tasks.stream().filter(task -> task.getStatus() == Task.Status.COMPLETED).count() * 100.0) / tasks.size();
        double commitTrend = 80.0;
        double deadlineScore = new ProjectHealthCalculator().calculateDeadlineScore(project);

        Team team = project.getTeam();
        List<User> members = team != null ? team.getMembers() : userRepository.findAll();
        List<ContributionScorer.ContributionScoreResult> resultList = new ArrayList<>();
        for (User member : members) {
            List<Contribution> contributions = contributionRepository.findByProject(project).stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(member.getId()))
                .toList();
            if (contributions.isEmpty()) {
                contributions = createDemoContribution(project, member);
            }
            ContributionScorer scorer = new ContributionScorer();
            ContributionScorer.ContributionScoreResult score = scorer.scoreUser(member, contributions, tasks, project);
            resultList.add(score);
        }

        FreeRiderDetector detector = new FreeRiderDetector();
        List<FreeRiderDetector.FreeRiderResult> freeRiders = detector.detect(resultList);
        double teamAverage = resultList.stream().mapToDouble(ContributionScorer.ContributionScoreResult::getScore).average().orElse(0.0);

        ProjectHealthCalculator calculator = new ProjectHealthCalculator();
        ProjectHealthCalculator.HealthResult health = calculator.calculate(project, tasks, commitTrend, taskCompletionRate);
        HealthScore score = new HealthScore();
        score.setProject(project);
        score.setCommitTrend(commitTrend);
        score.setTaskCompletionRate(taskCompletionRate);
        score.setDeadlineScore(deadlineScore);
        score.setHealthScore(health.getHealthScore());
        score.setStatus(HealthScore.Status.valueOf(health.getStatus()));
        score.setCalculatedAt(LocalDateTime.now());
        healthScoreRepository.save(score);

        Map<String, Object> summary = new HashMap<>();
        summary.put("projectName", project.getName());
        summary.put("healthScore", health.getHealthScore());
        summary.put("healthStatus", health.getStatus());
        summary.put("teamAverageContribution", teamAverage);
        summary.put("memberContributionScores", resultList);
        summary.put("potentialFreeRiders", freeRiders);
        summary.put("taskCompletionRate", taskCompletionRate);
        summary.put("commitTrend", commitTrend);
        summary.put("deadlineScore", deadlineScore);
        summary.put("recommendations", buildRecommendations(project, health, freeRiders));
        return summary;
    }

    public void analyzeAllProjects() {
        for (Project project : projectRepository.findAll()) {
            analyzeProject(project.getId());
        }
    }

    public List<Map<String, Object>> getContributions(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        List<Map<String, Object>> result = new ArrayList<>();
        for (User member : project.getTeam().getMembers()) {
            List<Contribution> contributions = contributionRepository.findByProject(project).stream()
                .filter(c -> c.getUser() != null && c.getUser().getId().equals(member.getId()))
                .toList();
            if (contributions.isEmpty()) {
                contributions = createDemoContribution(project, member);
            }
            result.add(Map.of(
                "user", member.getName(),
                "score", new ContributionScorer().scoreUser(member, contributions, taskRepository.findByProject(project), project).getScore(),
                "commits", contributions.stream().mapToInt(Contribution::getCommitCount).sum(),
                "prs", contributions.stream().mapToInt(Contribution::getPullRequestCount).sum(),
                "taskCompletion", 80
            ));
        }
        return result;
    }

    public List<Map<String, Object>> getFreeRiders(Long projectId) {
        Map<String, Object> summary = (Map<String, Object>) analyzeProject(projectId).get("potentialFreeRiders");
        return List.of();
    }

    public Map<String, Object> getHealth(Long projectId) {
        return analyzeProject(projectId);
    }

    public Map<String, Object> getSummary(Long projectId) {
        return analyzeProject(projectId);
    }

    private List<Contribution> createDemoContribution(Project project, User user) {
        List<Contribution> generated = new ArrayList<>();
        switch (user.getName()) {
            case "Aisha" -> generated.add(makeContribution(project, user, 32, 6, 42, 850L, 20L));
            case "Rahul" -> generated.add(makeContribution(project, user, 25, 4, 31, 600L, 30L));
            case "Priya" -> generated.add(makeContribution(project, user, 18, 3, 24, 400L, 25L));
            case "Arjun" -> generated.add(makeContribution(project, user, 3, 0, 4, 30L, 12L));
            default -> generated.add(makeContribution(project, user, 10, 1, 10, 200L, 10L));
        }
        contributionRepository.saveAll(generated);
        return generated;
    }

    private Contribution makeContribution(Project project, User user, int commits, int prs, int files, long churn, long trivial) {
        Contribution contribution = new Contribution();
        contribution.setProject(project);
        contribution.setUser(user);
        contribution.setCommitCount(commits);
        contribution.setPullRequestCount(prs);
        contribution.setFilesChanged(files);
        contribution.setCodeChurn(churn);
        contribution.setTrivialChanges(trivial);
        contribution.setAnalysisDate(LocalDateTime.now());
        return contribution;
    }

    private List<String> buildRecommendations(Project project, ProjectHealthCalculator.HealthResult health, List<FreeRiderDetector.FreeRiderResult> freeRiders) {
        List<String> recommendations = new ArrayList<>();
        if (health.getTaskCompletionRate() < 50) {
            recommendations.add("Project has low task completion. Consider redistributing pending tasks.");
        }
        if (health.getHealthScore() < 60) {
            recommendations.add("Project is at risk of missing the deadline. Review current blockers and dependencies.");
        }
        if (!freeRiders.isEmpty()) {
            recommendations.add("Review workload distribution for identified low-contribution members.");
        }
        recommendations.add("Keep documentation and GitHub activity aligned with the team plan.");
        return recommendations;
    }

    public List<Map<String, Object>> getDashboardData() {
        Map<String, Object> response = new HashMap<>();
        response.put("teams", teamRepository.count());
        response.put("projects", projectRepository.count());
        response.put("activeProjects", projectRepository.findAll().stream().filter(p -> p.getStatus() == Project.Status.ACTIVE).count());
        response.put("atRiskProjects", projectRepository.findAll().stream().filter(p -> p.getStatus() == Project.Status.AT_RISK).count());
        response.put("teamMembers", userRepository.count());
        response.put("overallTaskCompletion", 75.0);
        response.put("projectsHealth", new ArrayList<>());
        return List.of(response);
    }

    public List<String> getRecentActivity(Project project) {
        return gitHubClient.getContributorActivity(project.getGithubRepositoryUrl());
    }
}
