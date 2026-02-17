package com.budget_planner.budget_planner.expense.domain;

import com.budget_planner.budget_planner.account.domain.Account;
import com.budget_planner.budget_planner.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "expenses")
@SuppressWarnings("FieldMayBeFinal")
public class Expense {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank
    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode;

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "amount_usd", nullable = false)
    private BigDecimal amountUsd;

    @NotNull
    @DecimalMin("0.000001")
    @Column(name = "exchange_rate_used", nullable = false)
    private BigDecimal exchangeRateUsed;

    @NotNull
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Size(max = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(name = "expense_tags", joinColumns = @JoinColumn(name = "expense_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    public void prePersist () {
        this.createdAt = Instant.now();
    }
}
