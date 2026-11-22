package com.pkg.tasktracker.dto;

import com.pkg.tasktracker.entity.Attachment;

import java.time.Instant;

public record AttachmentResponse(
        Long id,
        Long taskId,
        String filename,
        String contentType,
        long sizeBytes,
        Long uploadedById,
        String uploadedByName,
        Instant createdAt,
        String downloadUrl) {

    public static AttachmentResponse from(Attachment a) {
        return new AttachmentResponse(
                a.getId(),
                a.getTask().getId(),
                a.getOriginalFilename(),
                a.getContentType(),
                a.getSizeBytes(),
                a.getUploadedBy().getId(),
                a.getUploadedBy().getFullName(),
                a.getCreatedAt(),
                "/api/v1/attachments/" + a.getId() + "/download");
    }
}