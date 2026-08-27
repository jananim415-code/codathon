package com.projectsphere.controller;

import com.projectsphere.service.IntelligenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/intelligence")
public class IntelligenceController {

    private final IntelligenceService intelligenceService;

    public IntelligenceController(IntelligenceService intelligenceService) {
        this.intelligenceService = intelligenceService;
    }

    @GetMapping("/projects/{projectId}/contributions")
    public ResponseEntity<List<Map<String, Object>>> getContributions(@PathVariable Long projectId) {
        return ResponseEntity.ok(intelligenceService.getContributions(projectId));
    }

    @GetMapping("/projects/{projectId}/free-riders")
    public ResponseEntity<List<Map<String, Object>>> getFreeRiders(@PathVariable Long projectId) {
        return ResponseEntity.ok(intelligenceService.getFreeRiders(projectId));
    }

    @GetMapping("/projects/{projectId}/health")
    public ResponseEntity<Map<String, Object>> getHealth(@PathVariable Long projectId) {
        return ResponseEntity.ok(intelligenceService.getHealth(projectId));
    }

    @GetMapping("/projects/{projectId}/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@PathVariable Long projectId) {
        return ResponseEntity.ok(intelligenceService.getSummary(projectId));
    }

    @PostMapping("/analyze/{projectId}")
    public ResponseEntity<Map<String, Object>> analyzeProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(intelligenceService.analyzeProject(projectId));
    }

    @PostMapping("/analyze-all")
    public ResponseEntity<Map<String, Object>> analyzeAll() {
        intelligenceService.analyzeAllProjects();
        return ResponseEntity.ok(Map.of("status", "success", "message", "All projects analyzed"));
    }
}
