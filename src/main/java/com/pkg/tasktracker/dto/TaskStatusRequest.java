package com.pkg.tasktracker.dto;

import com.pkg.tasktracker.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record TaskStatusRequest(@NotNull TaskStatus status) {}
