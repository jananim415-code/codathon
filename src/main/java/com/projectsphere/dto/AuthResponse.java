package com.projectsphere.dto;

public record AuthResponse(String token, String tokenType, long expiresIn, UserResponse user) {}
