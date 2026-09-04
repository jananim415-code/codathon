package com.projectsphere.dto;

import com.projectsphere.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
    Long id,
    String name,
    String email,
    User.Role role,
    String githubUsername,
    LocalDateTime createdAt,
    List<ApiReference> teams
) {
}
