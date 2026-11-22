package com.pkg.tasktracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
        String uploadDir,
        long maxFileSizeBytes,
        int maxFilesPerTask,
        List<String> allowedContentTypes
) {
}
