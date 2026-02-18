package com.budget_planner.budget_planner.expense.domain;

import com.budget_planner.budget_planner.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Period period;

    @NotNull
    @DecimalMin("0.01")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "start_date", nullable = false)
    @FutureOrPresent
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    @FutureOrPresent
    private LocalDate endDate;

    @SuppressWarnings("FieldMayBeFinal")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected Budget () {}

    public Budget(Period period, BigDecimal amount, LocalDate startDate, User user, Category category) {
        this.period = period;
        this.amount = amount;
        this.startDate = startDate;
        this.user = user;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @PrePersist
    public void prePersist () {
        this.createdAt = Instant.now();
        this.endDate = calculateEndDate();
    }

    @PreUpdate
    public void preUpdate () {
        this.endDate = calculateEndDate();
    }

    public LocalDate calculateEndDate() {
        if (period == null || startDate == null)
            throw new IllegalStateException("Period and startDate must be set before persisting");

        return switch (this.period) {
            case WEEKLY -> this.startDate.plusDays(6);
            case MONTHLY -> this.startDate.plusMonths(1).minusDays(1);
            case YEARLY -> this.startDate.plusYears(1).minusDays(1);
        };
    }
}
