package com.pkg.tasktracker.service;

import com.pkg.tasktracker.entity.ProjectRole;
import com.pkg.tasktracker.exception.ForbiddenException;
import com.pkg.tasktracker.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessService {
    @Autowired
    private final ProjectMemberRepository members;

    public void requireMember(Long projectId, Long userId) throws ForbiddenException {
        if (!members.existsByProjectIdAndUserId(projectId, userId))
            throw new ForbiddenException("You are not a member of this project");
    }
    public void requireOwner(Long projectId, Long userId) throws ForbiddenException {
        var m = members.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ForbiddenException("Not a member"));
        if (m.getRole() != ProjectRole.OWNER) throw new ForbiddenException("Owner only");
    }
    public boolean isOwner(Long projectId, Long userId) throws ForbiddenException {
        var m = members.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ForbiddenException("Not a member"));
        return m.getRole() == ProjectRole.OWNER;
    }
}
