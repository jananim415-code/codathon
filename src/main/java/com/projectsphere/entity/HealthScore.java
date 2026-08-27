package com.projectsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HealthScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private double commitTrend;
    private double taskCompletionRate;
    private double deadlineScore;
    private double healthScore;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime calculatedAt;

    public enum Status {
        HEALTHY,
        MODERATE,
        AT_RISK,
        CRITICAL
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public double getCommitTrend() { return commitTrend; }
    public void setCommitTrend(double commitTrend) { this.commitTrend = commitTrend; }
    public double getTaskCompletionRate() { return taskCompletionRate; }
    public void setTaskCompletionRate(double taskCompletionRate) { this.taskCompletionRate = taskCompletionRate; }
    public double getDeadlineScore() { return deadlineScore; }
    public void setDeadlineScore(double deadlineScore) { this.deadlineScore = deadlineScore; }
    public double getHealthScore() { return healthScore; }
    public void setHealthScore(double healthScore) { this.healthScore = healthScore; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
