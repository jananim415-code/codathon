package com.projectsphere.dto;

import jakarta.validation.constraints.NotBlank;

public record TeamRequest(
    @NotBlank String name,
    String description
) {
}
