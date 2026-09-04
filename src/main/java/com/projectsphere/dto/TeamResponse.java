package com.projectsphere.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TeamResponse(
    Long id,
    String name,
    String description,
    LocalDateTime createdAt,
    List<ApiReference> members,
    List<ApiReference> projects
) {
}
