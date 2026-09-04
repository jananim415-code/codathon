package com.projectsphere.dto;

import com.projectsphere.entity.Task;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TaskRequest(
    @NotBlank String title,
    String description,
    Task.Status status,
    Task.Priority priority,
    LocalDate dueDate,
    Long assignedUserId,
    Long projectId
) {
}
