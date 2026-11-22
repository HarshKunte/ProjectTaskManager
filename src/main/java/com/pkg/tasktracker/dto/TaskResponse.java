package com.pkg.tasktracker.dto;

import com.pkg.tasktracker.entity.Priority;
import com.pkg.tasktracker.entity.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        TaskStatus status,
        Priority priority,
        LocalDate dueDate,
        Long assigneeId,
        String assigneeName,
        Long createdById,
        Instant createdAt,
        Instant updatedAt,
        Instant completedAt
) {}
