package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.*;
import com.pkg.tasktracker.entity.TaskStatus;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/projects/{pid}/tasks")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long pid,
                                                    @Valid @RequestBody CreateTaskRequest request)
            throws ForbiddenException {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(pid, request));
    }

    @GetMapping("/projects/{pid}/tasks")
    public ResponseEntity<TaskPageResponse> listProjectTasks(
            @PathVariable Long pid,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) LocalDate dueBefore,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
            throws ForbiddenException {
        return ResponseEntity.ok(taskService.listProjectTasks(pid, status, assigneeId, q, dueBefore, pageable));
    }

    @GetMapping("/tasks/mine")
    public ResponseEntity<TaskPageResponse> listMineTasks(
            @RequestParam(required = false) TaskStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.listMine(status, pageable));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id) throws ForbiddenException {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                    @RequestBody UpdateTaskRequest request)
            throws ForbiddenException {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/tasks/{id}/assignee")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long id,
                                                    @Valid @RequestBody TaskAssigneeRequest request)
            throws ForbiddenException {
        return ResponseEntity.ok(taskService.assignTask(id, request));
    }

    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<TaskResponse> changeStatus(@PathVariable Long id,
                                                      @Valid @RequestBody TaskStatusRequest request)
            throws ForbiddenException {
        return ResponseEntity.ok(taskService.changeStatus(id, request));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) throws ForbiddenException {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
