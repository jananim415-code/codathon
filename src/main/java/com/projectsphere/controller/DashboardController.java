package com.projectsphere.controller;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Task;
import com.projectsphere.entity.HealthScore;
import com.projectsphere.repository.ProjectRepository;
import com.projectsphere.repository.TeamRepository;
import com.projectsphere.repository.UserRepository;
import com.projectsphere.repository.TaskRepository;
import com.projectsphere.repository.HealthScoreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final HealthScoreRepository healthScoreRepository;

    public DashboardController(ProjectRepository projectRepository, TeamRepository teamRepository,
                               UserRepository userRepository, TaskRepository taskRepository,
                               HealthScoreRepository healthScoreRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.healthScoreRepository = healthScoreRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalTeams", teamRepository.count());
        response.put("totalProjects", projectRepository.count());
        var projects = projectRepository.findAll();
        response.put("activeProjects", projects.stream().filter(p -> p.getStatus() == Project.Status.ACTIVE).count());
        response.put("atRiskProjects", projects.stream().filter(p -> p.getStatus() == Project.Status.AT_RISK).count());
        response.put("totalTeamMembers", userRepository.count());
        var tasks = taskRepository.findAll();
        response.put("overallTaskCompletion", completionPercentage(tasks));
        response.put("projectsHealth", projects.stream().map(this::projectHealth).toList());
        return ResponseEntity.ok(response);
    }

    private double completionPercentage(java.util.List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        return tasks.stream().filter(task -> task.getStatus() == Task.Status.COMPLETED).count() * 100.0 / tasks.size();
    }

    private Map<String, Object> projectHealth(Project project) {
        HealthScore score = healthScoreRepository.findTopByProjectOrderByCalculatedAtDesc(project).orElse(null);
        return Map.of(
            "name", project.getName(),
            "health", score == null ? 0.0 : score.getHealthScore(),
            "status", score == null || score.getStatus() == null ? "NOT_ANALYZED" : score.getStatus().name());
    }
}
