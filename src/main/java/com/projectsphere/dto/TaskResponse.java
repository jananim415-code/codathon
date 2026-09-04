package com.projectsphere.dto;

import com.projectsphere.entity.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    String title,
    String description,
    Task.Status status,
    Task.Priority priority,
    LocalDate dueDate,
    LocalDateTime completedAt,
    LocalDateTime createdAt,
    ApiReference assignedUser,
    ApiReference project
) {
}
