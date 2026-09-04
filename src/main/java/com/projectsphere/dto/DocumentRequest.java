package com.projectsphere.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentRequest(
    @NotBlank String title,
    String content,
    Long projectId,
    Long createdById
) {
}
