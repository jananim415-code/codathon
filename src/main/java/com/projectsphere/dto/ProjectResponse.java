package com.projectsphere.dto;

import com.projectsphere.entity.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
    Long id,
    String name,
    String description,
    LocalDate deadline,
    String githubRepositoryUrl,
    Project.Status status,
    LocalDateTime createdAt,
    ApiReference team
) {
}
