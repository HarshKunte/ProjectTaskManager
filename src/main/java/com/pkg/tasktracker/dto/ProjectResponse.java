package com.pkg.tasktracker.dto;

public record ProjectResponse(
        Long id, String name, String description, Long ownerId
) {
}
