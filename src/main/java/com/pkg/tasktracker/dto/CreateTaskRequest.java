package com.pkg.tasktracker.dto;

import com.pkg.tasktracker.entity.Priority;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank String title,
        String description,
        LocalDate dueDate,
        Priority priority,
        Long assigneeId
) {}
