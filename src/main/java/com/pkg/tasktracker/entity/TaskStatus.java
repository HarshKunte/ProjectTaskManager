package com.pkg.tasktracker.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TaskStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED;

    @JsonCreator
    public static TaskStatus fromJson(String value) {
        if (value == null) return null;
        String normalized = value.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        if ("COMPLETE".equals(normalized) || "CLOSED".equals(normalized) || "DONE".equals(normalized)) {
            normalized = "COMPLETED";
        }
        return TaskStatus.valueOf(normalized);
    }
}
