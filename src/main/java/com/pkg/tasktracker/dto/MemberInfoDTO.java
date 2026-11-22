package com.pkg.tasktracker.dto;

import com.pkg.tasktracker.entity.ProjectRole;

public record MemberInfoDTO(
        Long memberId, String memberEmail, ProjectRole role
) {
}
