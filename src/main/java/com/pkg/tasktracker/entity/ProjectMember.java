package com.pkg.tasktracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_members", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(columnNames = {"project_id", "user_id"})
})

@Getter @Setter @NoArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.DETACH) private Project project;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @Enumerated(EnumType.STRING) private ProjectRole role = ProjectRole.MEMBER;
}
