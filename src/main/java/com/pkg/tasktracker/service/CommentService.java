package com.pkg.tasktracker.service;

import com.pkg.tasktracker.controller.helper.CurrentUser;
import com.pkg.tasktracker.dto.CommentPageResponse;
import com.pkg.tasktracker.dto.CommentResponse;
import com.pkg.tasktracker.dto.TaskPageResponse;
import com.pkg.tasktracker.dto.TaskResponse;
import com.pkg.tasktracker.entity.Comment;
import com.pkg.tasktracker.entity.Task;
import com.pkg.tasktracker.entity.User;
import com.pkg.tasktracker.exception.ConflictException;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CurrentUser currentUser;
    private final TaskService taskService;
    private final AccessService accessService;


    @Transactional
    public CommentResponse addComment(Long taskId, String description) throws ForbiddenException {
        User u = currentUser.get();
        Task task = taskService.findTask(taskId);

        //should be a member of the project to add a comment
        accessService.requireMember(task.getProject().getId(), u.getId());

        Comment comment = new Comment();
        comment.setBody(description);
        comment.setTask(task);
        comment.setAuthor(u);

        commentRepository.save(comment);

        return toResponse(comment);

    }

    private CommentResponse toResponse(Comment comment){
        return new CommentResponse(comment.getId(), comment.getBody(), comment.getAuthor().getId(), comment.getAuthor().getFullName(), comment.getTask().getId(), comment.getCreatedAt());
    }

    public CommentPageResponse getComments(Long taskId, Pageable pageable) throws ForbiddenException{
        User u = currentUser.get();
        Task task = taskService.findTask(taskId);

        //should be a member of the project to add a comment
        accessService.requireMember(task.getProject().getId(), u.getId());

        Page<Comment> page = commentRepository.findByTask(task, pageable);

        return toPage(page);
    }

    private CommentPageResponse toPage(Page<Comment> page) {
        return new CommentPageResponse(page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Transactional
    public void deleteComment(Long taskId, Long commentId) throws ConflictException, ForbiddenException {
        User u = currentUser.get();
        Task task = taskService.findTask(taskId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()-> new EntityNotFoundException("Comment does not exist."));


        if(comment.getAuthor().getId()!=u.getId() && !accessService.isOwner(task.getProject().getId(), u.getId())){
            throw new ConflictException("You cannot delete this comment as you are not the author or project owner");
        }

        commentRepository.delete(comment);
    }
}
