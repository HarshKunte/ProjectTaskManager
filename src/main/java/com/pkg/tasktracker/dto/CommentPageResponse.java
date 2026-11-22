package com.pkg.tasktracker.dto;

import java.util.List;

public record CommentPageResponse(
        List<CommentResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
