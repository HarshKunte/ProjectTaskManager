package com.pkg.tasktracker.dto;

public record AuthResponse(
        String token, String tokenType, long expiresInMs
) {
}
