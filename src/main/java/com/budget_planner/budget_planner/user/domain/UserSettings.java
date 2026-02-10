package com.budget_planner.budget_planner.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_settings", uniqueConstraints = { @UniqueConstraint(name = "uk_user_settings", columnNames = "user_id") })
@SuppressWarnings("FieldMayBeFinal")
public class UserSettings {

    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @Min(1)
    @Max(7)
    @Column(name = "week_start_day", nullable = false)
    private int weekStartDay;

    @NotBlank
    @Size(max = 10)
    private String language = "en";

    @NotBlank
    @Size(max = 20)
    private String theme = "light";

    @NotBlank
    @Size(max = 50)
    private String timezone = "UTC";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @NotNull
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}