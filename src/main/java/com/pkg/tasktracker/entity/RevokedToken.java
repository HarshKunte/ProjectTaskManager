package com.pkg.tasktracker.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
@Getter @Setter @NoArgsConstructor
public class RevokedToken {
    @Id
    private String jti;
    private Instant expiresAt;
}
