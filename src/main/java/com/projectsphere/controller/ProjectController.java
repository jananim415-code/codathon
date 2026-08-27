package com.projectsphere.controller;

import com.projectsphere.entity.Project;
import com.projectsphere.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> listProjects() {
        return ResponseEntity.ok(projectService.listProjects());
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProject(id));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @Valid @RequestBody Project project) {
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }

    @GetMapping("/projects/{id}/stats")
    public ResponseEntity<Map<String, Object>> getProjectStats(@PathVariable Long id) {
        Project project = projectService.getProject(id);
        Map<String, Object> stats = new HashMap<>();
        stats.put("projectName", project.getName());
        stats.put("team", project.getTeam() != null ? project.getTeam().getName() : "N/A");
        stats.put("deadline", project.getDeadline());
        stats.put("status", project.getStatus());
        stats.put("githubRepository", project.getGithubRepositoryUrl());
        stats.put("taskCount", project.getTasks() != null ? project.getTasks().size() : 0);
        stats.put("teamMembers", project.getTeam() != null ? project.getTeam().getMembers().size() : 0);
        return ResponseEntity.ok(stats);
    }
}
