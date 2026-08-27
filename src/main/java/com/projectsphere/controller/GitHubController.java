package com.projectsphere.controller;

import com.projectsphere.github.GitHubCommit;
import com.projectsphere.github.GitHubPullRequest;
import com.projectsphere.service.GitHubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/projects/{projectId}/github/commits")
    public ResponseEntity<List<GitHubCommit>> getCommits(@PathVariable Long projectId) {
        return ResponseEntity.ok(gitHubService.getCommits(projectId));
    }

    @GetMapping("/projects/{projectId}/github/pulls")
    public ResponseEntity<List<GitHubPullRequest>> getPullRequests(@PathVariable Long projectId) {
        return ResponseEntity.ok(gitHubService.getPullRequests(projectId));
    }

    @GetMapping("/projects/{projectId}/github/activity")
    public ResponseEntity<List<String>> getContributorActivity(@PathVariable Long projectId) {
        return ResponseEntity.ok(gitHubService.getContributorActivity(projectId));
    }

    @GetMapping("/github/demo-status")
    public ResponseEntity<Map<String, Object>> status() {
        return ResponseEntity.ok(Map.of("demoMode", true, "message", "Demo GitHub Data"));
    }
}
