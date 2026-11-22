package com.pkg.tasktracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User author;

    private Instant createdAt = Instant.now();
}
