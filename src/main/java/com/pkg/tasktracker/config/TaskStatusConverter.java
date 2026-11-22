package com.pkg.tasktracker.config;

import com.pkg.tasktracker.entity.TaskStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusConverter implements Converter<String, TaskStatus> {
    @Override
    public TaskStatus convert(String source) {
        if (source == null || source.isBlank()) return null;

        String normalized = source.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        if ("COMPLETE".equals(normalized) || "CLOSED".equals(normalized) || "DONE".equals(normalized)) {
            normalized = "COMPLETED";
        }
        return TaskStatus.valueOf(normalized);
    }
}
