package com.projectsphere.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    private LocalDate deadline;

    private String githubRepositoryUrl;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<Contribution> contributions = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    private List<HealthScore> healthScores = new ArrayList<>();

    public Project() {
        this.createdAt = LocalDateTime.now();
    }

    public enum Status {
        ACTIVE,
        COMPLETED,
        AT_RISK,
        STALLED
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getGithubRepositoryUrl() { return githubRepositoryUrl; }
    public void setGithubRepositoryUrl(String githubRepositoryUrl) { this.githubRepositoryUrl = githubRepositoryUrl; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
    public List<Contribution> getContributions() { return contributions; }
    public void setContributions(List<Contribution> contributions) { this.contributions = contributions; }
    public List<HealthScore> getHealthScores() { return healthScores; }
    public void setHealthScores(List<HealthScore> healthScores) { this.healthScores = healthScores; }
}
