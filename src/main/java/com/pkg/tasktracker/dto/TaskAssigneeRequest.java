package com.pkg.tasktracker.dto;

import jakarta.validation.constraints.NotNull;

public record TaskAssigneeRequest(@NotNull Long assigneeId) {}
