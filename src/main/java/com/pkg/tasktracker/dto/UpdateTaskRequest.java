package com.pkg.tasktracker.dto;

import com.pkg.tasktracker.entity.Priority;

import java.time.LocalDate;

public record UpdateTaskRequest(
        String title,
        String description,
        LocalDate dueDate,
        Priority priority,
        Long assigneeId,
        com.pkg.tasktracker.entity.TaskStatus status
) {}
