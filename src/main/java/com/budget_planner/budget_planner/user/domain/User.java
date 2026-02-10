package com.budget_planner.budget_planner.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "uk_user_email", columnNames = "email") })
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Size(max = 100)
    private String name;

    @Email
    @NotBlank
    @Column(nullable = false)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String password;

    @SuppressWarnings("FieldMayBeFinal")
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, UUID id, String email, String password, OffsetDateTime createdAt) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}