package com.pkg.tasktracker.dto;


import org.springframework.core.io.Resource;

/** Internal carrier from service to controller. */
public record DownloadableFile(Resource resource, String filename, String contentType, long sizeBytes) {}
