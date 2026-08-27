package com.projectsphere.service;

import com.projectsphere.entity.Project;
import com.projectsphere.github.GitHubClient;
import com.projectsphere.github.GitHubCommit;
import com.projectsphere.github.GitHubPullRequest;
import com.projectsphere.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;
    private final ProjectRepository projectRepository;

    public GitHubService(GitHubClient gitHubClient, ProjectRepository projectRepository) {
        this.gitHubClient = gitHubClient;
        this.projectRepository = projectRepository;
    }

    public Project syncRepository(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        if (project.getGithubRepositoryUrl() == null || project.getGithubRepositoryUrl().isBlank()) {
            project.setGithubRepositoryUrl(gitHubClient.getRepository());
        }
        return projectRepository.save(project);
    }

    public List<GitHubCommit> getCommits(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return gitHubClient.getCommits(project.getGithubRepositoryUrl());
    }

    public List<GitHubPullRequest> getPullRequests(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return gitHubClient.getPullRequests(project.getGithubRepositoryUrl());
    }

    public List<String> getContributorActivity(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        return gitHubClient.getContributorActivity(project.getGithubRepositoryUrl());
    }
}
