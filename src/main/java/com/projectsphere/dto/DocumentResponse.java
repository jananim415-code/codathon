package com.projectsphere.dto;

import java.time.LocalDateTime;

public record DocumentResponse(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    ApiReference project,
    ApiReference createdBy
) {
}
