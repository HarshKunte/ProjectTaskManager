package com.pkg.tasktracker.service;

import com.pkg.tasktracker.controller.helper.CurrentUser;
import com.pkg.tasktracker.dto.MemberInfoDTO;
import com.pkg.tasktracker.dto.ProjectMembersResponse;
import com.pkg.tasktracker.dto.ProjectRequestBody;
import com.pkg.tasktracker.entity.Project;
import com.pkg.tasktracker.entity.ProjectMember;
import com.pkg.tasktracker.entity.ProjectRole;
import com.pkg.tasktracker.entity.User;
import com.pkg.tasktracker.exception.ConflictException;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.repository.ProjectMemberRepository;
import com.pkg.tasktracker.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);
    @Autowired
    private CurrentUser currentUser;
    @Autowired
    private final ProjectRepository projectRepository;
    @Autowired
    private final ProjectMemberRepository projectMemberRepository;
    @Autowired
    private final AccessService accessService;
    @Autowired
    private final UserService userService;

    @Transactional
    public Project createProject(ProjectRequestBody projectRequestBody) {

        log.info("Creating project with name: {}", projectRequestBody.name());

        User u = currentUser.get();
        log.info("Creating project for user: {}", u.getEmail());
        Project project = new Project();
        project.setName(projectRequestBody.name());
        project.setDescription(projectRequestBody.description());
        project.setOwner(u);
        projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(project);
        projectMember.setUser(u);
        projectMember.setRole(ProjectRole.OWNER);
        projectMemberRepository.save(projectMember);

        return project;

    }

    public List<Project> getMyProjects(){
        User u = currentUser.get();
        return projectRepository.findByOwner(u);
    }

    @Transactional
    public void addMember(Long projectId, String email) throws ConflictException, ForbiddenException {
        log.info("Email: {}", email);
        accessService.requireOwner(projectId, currentUser.get().getId());
        User invitee = userService.getUserByEmail(email.toLowerCase());
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, invitee.getId()))
            throw new ConflictException("Already a member");
        ProjectMember m = new ProjectMember();
        m.setProject(projectRepository.getReferenceById(projectId));
        m.setUser(invitee);
        projectMemberRepository.save(m);
    }

    @Transactional
    public ProjectMembersResponse getProjectDetailsAndMembers(Long projectId) throws ForbiddenException, EntityNotFoundException {
        accessService.requireMember(projectId, currentUser.get().getId());
        Project p = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        List<MemberInfoDTO> memberInfos = members.stream().map(m -> new MemberInfoDTO(m.getUser().getId(), m.getUser().getEmail(), m.getRole())).toList();
        ProjectMembersResponse response = new ProjectMembersResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getOwner().getId(),
                memberInfos
        );
        return response;
    }

    @Transactional
    public void removeMember(Long projectId, Long userId) throws ForbiddenException {
        accessService.requireOwner(projectId, currentUser.get().getId());
        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new EntityNotFoundException("Member not found in the project");
        }
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

}
