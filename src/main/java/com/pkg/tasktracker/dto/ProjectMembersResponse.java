package com.pkg.tasktracker.dto;

import java.util.List;

public record ProjectMembersResponse(
        Long projectId, String projectName, String description, Long ownerId, List<MemberInfoDTO> members
) {
}
