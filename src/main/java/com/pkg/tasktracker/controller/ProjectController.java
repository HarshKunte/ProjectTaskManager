package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.AddMemberReqBody;
import com.pkg.tasktracker.dto.ProjectMembersResponse;
import com.pkg.tasktracker.dto.ProjectRequestBody;
import com.pkg.tasktracker.dto.ProjectResponse;
import com.pkg.tasktracker.entity.Project;
import com.pkg.tasktracker.exception.ConflictException;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller @RequiredArgsConstructor
public class ProjectController {

    @Autowired
    private final ProjectService projectService;

    @PostMapping("/projects")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequestBody requestBody) {
        Project createdProject = projectService.createProject(requestBody);
        return ResponseEntity.ok(new ProjectResponse(createdProject.getId(), createdProject.getName(), createdProject.getDescription(), createdProject.getOwner().getId()));
    }
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getMyProjects() {
        List<Project> projects = projectService.getMyProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectMembersResponse> getProjectById(@PathVariable Long id) throws ForbiddenException {
        ProjectMembersResponse response = projectService.getProjectDetailsAndMembers(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/projects/{id}/members")
    public ResponseEntity<String> addMember(@PathVariable Long id, @RequestBody AddMemberReqBody reqBody) throws ConflictException, ForbiddenException {
        projectService.addMember(id, reqBody.email());
        return ResponseEntity.ok("Project member added successfully");
    }

    @DeleteMapping("/projects/{id}/members/{userId}")
    public ResponseEntity<String> removeMember(@PathVariable Long id, @PathVariable Long userId) throws ConflictException, ForbiddenException {
        projectService.removeMember(id, userId);
        return ResponseEntity.ok("Project member removed successfully");
    }
}
