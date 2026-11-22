package com.pkg.tasktracker.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public record ProjectRequestBody (
        String name, String description
) {

}
