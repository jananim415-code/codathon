package com.projectsphere.dto;

import com.projectsphere.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
    @NotBlank String name,
    @Email @NotBlank String email,
    User.Role role,
    String githubUsername
) {
}
