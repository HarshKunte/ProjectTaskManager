package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.CommentPageResponse;
import com.pkg.tasktracker.dto.CommentRequestBody;
import com.pkg.tasktracker.dto.CommentResponse;
import com.pkg.tasktracker.entity.Comment;
import com.pkg.tasktracker.exception.ConflictException;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {


    private final CommentService commentService;

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long taskId, @RequestBody CommentRequestBody commentRequestBody) throws ForbiddenException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(taskId,commentRequestBody.description()));
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentPageResponse> getComments(@PathVariable Long taskId,
                                                           @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) throws ForbiddenException {
        return ResponseEntity.ok(commentService.getComments(taskId, pageable));
    }

    @DeleteMapping("/tasks/{taskId}/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long taskId, @PathVariable Long id) throws ConflictException, ForbiddenException {
        commentService.deleteComment(taskId, id);
        return ResponseEntity.noContent().build();
    }
}
