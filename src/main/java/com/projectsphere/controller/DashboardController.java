package com.projectsphere.controller;

import com.projectsphere.entity.Project;
import com.projectsphere.service.IntelligenceService;
import com.projectsphere.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final IntelligenceService intelligenceService;
    private final ProjectRepository projectRepository;

    public DashboardController(IntelligenceService intelligenceService, ProjectRepository projectRepository) {
        this.intelligenceService = intelligenceService;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalTeams", 1);
        response.put("totalProjects", projectRepository.count());
        response.put("activeProjects", projectRepository.findAll().stream().filter(p -> p.getStatus() == Project.Status.ACTIVE).count());
        response.put("atRiskProjects", projectRepository.findAll().stream().filter(p -> p.getStatus() == Project.Status.AT_RISK).count());
        response.put("totalTeamMembers", 4);
        response.put("overallTaskCompletion", 75.0);
        response.put("projectsHealth", new Object[] {
            Map.of("name", "Smart Campus Assistant", "health", 84, "status", "HEALTHY")
        });
        return ResponseEntity.ok(response);
    }
}
