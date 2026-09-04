package com.projectsphere.dto;

import com.projectsphere.entity.Project;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ProjectRequest(
    @NotBlank String name,
    String description,
    LocalDate deadline,
    String githubRepositoryUrl,
    Project.Status status,
    Long teamId
) {
}
