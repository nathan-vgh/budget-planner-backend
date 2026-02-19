package com.budget_planner.budget_planner.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
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
    private Instant createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    private UserSettings settings;

    protected User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.settings = new UserSettings();
        this.settings.setUser(this);
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setSettings(Currency currency, DayOfWeek weekStartDay, Locale language, Theme theme) {
        if(currency != null)
            this.settings.setCurrency(currency);

        if(weekStartDay != null)
            this.settings.setWeekStartDay(weekStartDay);

        if(language != null)
            this.settings.setLanguage(language);

        if(theme != null)
            this.settings.setTheme(theme);
    }

    public UserSettings getSettings() {
        return settings;
    }

    @PrePersist
    public void prePersist () {
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}