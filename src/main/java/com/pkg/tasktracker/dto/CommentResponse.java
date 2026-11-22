package com.pkg.tasktracker.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

public record CommentResponse (
    Long commentId,
    String description,
    Long authorId,
    String authorName,
    Long taskId,
    Instant createdAt
){}
