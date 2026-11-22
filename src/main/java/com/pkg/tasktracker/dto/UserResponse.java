package com.pkg.tasktracker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record UserResponse(Long id, String email, String fullName) {
}
