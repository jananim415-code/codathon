package com.projectsphere.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int commitCount;
    private int pullRequestCount;
    private int filesChanged;
    private long codeChurn;
    private long trivialChanges;
    private double contributionScore;
    private LocalDateTime analysisDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getCommitCount() { return commitCount; }
    public void setCommitCount(int commitCount) { this.commitCount = commitCount; }
    public int getPullRequestCount() { return pullRequestCount; }
    public void setPullRequestCount(int pullRequestCount) { this.pullRequestCount = pullRequestCount; }
    public int getFilesChanged() { return filesChanged; }
    public void setFilesChanged(int filesChanged) { this.filesChanged = filesChanged; }
    public long getCodeChurn() { return codeChurn; }
    public void setCodeChurn(long codeChurn) { this.codeChurn = codeChurn; }
    public long getTrivialChanges() { return trivialChanges; }
    public void setTrivialChanges(long trivialChanges) { this.trivialChanges = trivialChanges; }
    public double getContributionScore() { return contributionScore; }
    public void setContributionScore(double contributionScore) { this.contributionScore = contributionScore; }
    public LocalDateTime getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDateTime analysisDate) { this.analysisDate = analysisDate; }
}
