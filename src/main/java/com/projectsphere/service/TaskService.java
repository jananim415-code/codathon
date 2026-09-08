package com.projectsphere.service;

import com.projectsphere.entity.Project;
import com.projectsphere.entity.Task;
import com.projectsphere.entity.User;
import com.projectsphere.exception.ResourceNotFoundException;
import com.projectsphere.repository.ProjectRepository;
import com.projectsphere.repository.TaskRepository;
import com.projectsphere.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(@Valid Task task) {
        if (task.getProject() != null && task.getProject().getId() != null) {
            Project project = projectRepository.findById(task.getProject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
            task.setProject(project);
        }
        if (task.getAssignedUser() != null && task.getAssignedUser().getId() != null) {
            User user = userRepository.findById(task.getAssignedUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedUser(user);
        }
        return taskRepository.save(task);
    }

    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task getTask(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    public List<Task> getTasksByProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return taskRepository.findByProject(project);
    }

    public Task updateTask(Long id, Task updatedTask) {
        Task existing = getTask(id);
        if (updatedTask.getTitle() != null) existing.setTitle(updatedTask.getTitle());
        if (updatedTask.getDescription() != null) existing.setDescription(updatedTask.getDescription());
        if (updatedTask.getPriority() != null) existing.setPriority(updatedTask.getPriority());
        if (updatedTask.getStatus() != null) {
            existing.setStatus(updatedTask.getStatus());
            if (updatedTask.getStatus() == Task.Status.COMPLETED) {
                existing.setCompletedAt(LocalDateTime.now());
            }
        }
        if (updatedTask.getDueDate() != null) existing.setDueDate(updatedTask.getDueDate());
        if (updatedTask.getAssignedUser() != null && updatedTask.getAssignedUser().getId() != null) {
            User user = userRepository.findById(updatedTask.getAssignedUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            existing.setAssignedUser(user);
        }
        return taskRepository.save(existing);
    }

    public void deleteTask(Long id) {
        Task task = getTask(id);
        taskRepository.delete(task);
    }
}
