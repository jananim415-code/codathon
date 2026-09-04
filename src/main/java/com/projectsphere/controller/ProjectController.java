package com.projectsphere.controller;

import com.projectsphere.dto.EntityDtoMapper;
import com.projectsphere.dto.ProjectRequest;
import com.projectsphere.dto.ProjectResponse;
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
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(projectService.createProject(EntityDtoMapper.toEntity(request))));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> listProjects() {
        return ResponseEntity.ok(projectService.listProjects().stream().map(EntityDtoMapper::toResponse).toList());
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(projectService.getProject(id)));
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(projectService.updateProject(id, EntityDtoMapper.toEntity(request))));
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
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
