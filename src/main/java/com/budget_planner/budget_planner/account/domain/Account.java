package com.budget_planner.budget_planner.account.domain;

import com.budget_planner.budget_planner.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts", uniqueConstraints = { @UniqueConstraint(name = "uk_user_account", columnNames = {"user_id", "name"}) })
public class Account {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "CASH|DEBIT|CREDIT")
    private String type;

    @SuppressWarnings("FieldMayBeFinal")
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
