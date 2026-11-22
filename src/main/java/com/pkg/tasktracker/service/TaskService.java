package com.pkg.tasktracker.service;

import com.pkg.tasktracker.controller.helper.CurrentUser;
import com.pkg.tasktracker.dto.*;
import com.pkg.tasktracker.entity.*;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.repository.ProjectMemberRepository;
import com.pkg.tasktracker.repository.ProjectRepository;
import com.pkg.tasktracker.repository.TaskRepository;
import com.pkg.tasktracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final AccessService accessService;
    private final CurrentUser currentUser;
    private final AttachmentService attachmentService;

    @Transactional
    public TaskResponse createTask(Long projectId, CreateTaskRequest request) throws ForbiddenException {
        User current = currentUser.get();
        accessService.requireMember(projectId, current.getId());
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        Task task = new Task();
        task.setProject(project);
        task.setCreatedBy(current);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority() == null ? Priority.LOW : request.priority());
        if (request.assigneeId() != null) task.setAssignee(resolveProjectAssignee(projectId, request.assigneeId()));

        return toResponse(taskRepository.save(task));
    }

    public TaskPageResponse listProjectTasks(Long projectId, TaskStatus status, Long assigneeId,
                                              String q, LocalDate dueBefore, Pageable pageable) throws ForbiddenException {
        accessService.requireMember(projectId, currentUser.get().getId());
        Page<Task> page = taskRepository.searchProjectTasks(projectId, status, assigneeId,
                q == null || q.isBlank() ? "" : q.trim(), dueBefore, pageable);
        return toPage(page);
    }

    public TaskPageResponse listMine(TaskStatus status, Pageable pageable) {
        return toPage(taskRepository.findMine(currentUser.get().getId(), status, pageable));
    }

    public TaskResponse getTask(Long taskId) throws ForbiddenException {
        Task task = findTask(taskId);
        accessService.requireMember(task.getProject().getId(), currentUser.get().getId());
        return toResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) throws ForbiddenException {
        Task task = findTask(taskId);
        accessService.requireMember(task.getProject().getId(), currentUser.get().getId());

        if (request.title() != null) {
            if (request.title().isBlank()) throw new IllegalArgumentException("Title must not be blank");
            task.setTitle(request.title());
        }
        if (request.description() != null) task.setDescription(request.description());
        if (request.dueDate() != null) task.setDueDate(request.dueDate());
        if (request.priority() != null) task.setPriority(request.priority());
        if (request.assigneeId() != null) {
            task.setAssignee(resolveProjectAssignee(task.getProject().getId(), request.assigneeId()));
        }
        if (request.status() != null) applyStatus(task, request.status());
        task.setUpdatedAt(Instant.now());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse assignTask(Long taskId, TaskAssigneeRequest request) throws ForbiddenException {
        Task task = findTask(taskId);
        accessService.requireMember(task.getProject().getId(), currentUser.get().getId());
        task.setAssignee(resolveProjectAssignee(task.getProject().getId(), request.assigneeId()));
        task.setUpdatedAt(Instant.now());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse changeStatus(Long taskId, TaskStatusRequest request) throws ForbiddenException {
        Task task = findTask(taskId);
        accessService.requireMember(task.getProject().getId(), currentUser.get().getId());
        applyStatus(task, request.status());
        task.setUpdatedAt(Instant.now());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId) throws ForbiddenException {
        Task task = findTask(taskId);
        accessService.requireMember(task.getProject().getId(), currentUser.get().getId());
        attachmentService.deleteAllForTask(taskId);
        taskRepository.delete(task);
    }

    public Task findTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    private User resolveProjectAssignee(Long projectId, Long assigneeId) {
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, assigneeId)) {
            throw new EntityNotFoundException("Assignee is not a member of the project");
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private void applyStatus(Task task, TaskStatus status) {
        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            if (task.getCompletedAt() == null) task.setCompletedAt(Instant.now());
        } else {
            task.setCompletedAt(null);
        }
    }

    private TaskPageResponse toPage(Page<Task> page) {
        return new TaskPageResponse(page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private TaskResponse toResponse(Task task) {
        User assignee = task.getAssignee();
        return new TaskResponse(task.getId(), task.getProject().getId(), task.getTitle(), task.getDescription(),
                task.getStatus(), task.getPriority(), task.getDueDate(),
                assignee == null ? null : assignee.getId(), assignee == null ? null : assignee.getFullName(),
                task.getCreatedBy().getId(), task.getCreatedAt(), task.getUpdatedAt(), task.getCompletedAt());
    }
}
