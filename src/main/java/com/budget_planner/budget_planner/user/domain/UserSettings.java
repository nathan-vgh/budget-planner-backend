package com.budget_planner.budget_planner.user.domain;

import com.budget_planner.budget_planner.common.converter.CurrencyConverter;
import com.budget_planner.budget_planner.user.persist.converter.LocaleConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "user_settings")
@SuppressWarnings("FieldMayBeFinal")
public class UserSettings {

    @Id
    private UUID id;

    @NotNull
    @Convert(converter = CurrencyConverter.class)
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency = Currency.getInstance("USD");

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "week_start_day", nullable = false)
    private DayOfWeek weekStartDay = DayOfWeek.MONDAY;

    @NotNull
    @Convert(converter = LocaleConverter.class)
    @Column(name = "language", length = 10, nullable = false)
    private Locale language = Locale.forLanguageTag("en");

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Theme theme = Theme.SYSTEM;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @MapsId
    @NotNull
    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserSettings() {}

    public UUID getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currencyCode) {
        this.currency = currencyCode;
    }

    public DayOfWeek getWeekStartDay() {
        return weekStartDay;
    }

    public void setWeekStartDay(DayOfWeek weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PrePersist
    public void prePersist () {
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSettings userSettings)) return false;
        return id != null && id.equals(userSettings.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}