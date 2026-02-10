package com.budget_planner.budget_planner.expense.domain;

import com.budget_planner.budget_planner.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tags", uniqueConstraints = { @UniqueConstraint(name = "uk_user_tag", columnNames = {"user_id", "name"}) })
public class Tag {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @SuppressWarnings("FieldMayBeFinal")
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
