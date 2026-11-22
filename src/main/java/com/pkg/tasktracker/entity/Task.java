package com.pkg.tasktracker.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name = "tasks")
@Getter @Setter @NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String title;
    @Column(columnDefinition = "text") private String description;
    @Enumerated(EnumType.STRING) private TaskStatus status = TaskStatus.OPEN;
    @Enumerated(EnumType.STRING) private Priority priority = Priority.LOW;
    private LocalDate dueDate;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Project project;
    @ManyToOne(fetch = FetchType.LAZY) private User assignee;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User createdBy;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Instant completedAt;

    @PreUpdate void touch() { this.updatedAt = Instant.now(); }
}
