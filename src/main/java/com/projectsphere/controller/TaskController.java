package com.projectsphere.controller;

import com.projectsphere.dto.EntityDtoMapper;
import com.projectsphere.dto.TaskRequest;
import com.projectsphere.dto.TaskResponse;
import com.projectsphere.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(taskService.createTask(EntityDtoMapper.toEntity(request))));
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponse>> listTasks() {
        return ResponseEntity.ok(taskService.listTasks().stream().map(EntityDtoMapper::toResponse).toList());
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId).stream().map(EntityDtoMapper::toResponse).toList());
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(EntityDtoMapper.toResponse(taskService.updateTask(id, EntityDtoMapper.toEntity(request))));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
