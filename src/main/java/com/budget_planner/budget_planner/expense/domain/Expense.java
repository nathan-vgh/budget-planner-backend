package com.budget_planner.budget_planner.expense.domain;

import com.budget_planner.budget_planner.account.domain.Account;
import com.budget_planner.budget_planner.user.domain.User;
import com.budget_planner.budget_planner.common.converter.CurrencyConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "expenses", indexes = {
            @Index(name = "idx_expense_user", columnList = "user_id"),
            @Index(name = "idx_expense_date", columnList = "expense_date"),
            @Index(name = "idx_expense_category", columnList = "category_id"),
            @Index(name = "idx_expense_account", columnList = "account_id")
})
@SuppressWarnings("FieldMayBeFinal")
public class Expense {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2)
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Convert(converter = CurrencyConverter.class)
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    @NotNull
    @Positive
    @Digits(integer = 17, fraction = 2)
    @Column(name = "amount_usd", nullable = false, precision = 19, scale = 2)
    private BigDecimal amountUsd;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 6)
    @Column(name = "exchange_rate_used", nullable = false, precision = 19, scale = 6)
    private BigDecimal exchangeRateUsed;

    @NotNull
    @PastOrPresent
    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @NotNull
    @NotBlank
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

    protected Expense () {}

    public Expense(BigDecimal amount, Currency currency, LocalDate expenseDate, String description, User user, Account account, Category category, Set<Tag> tags) {
        this.amount = amount;
        this.currency = currency;
        this.expenseDate = expenseDate;
        this.description = description;
        this.user = user;
        this.account = account;
        this.category = category;
        this.tags = tags;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAmountUsd() {
        return amountUsd;
    }

    public void setAmountUsd(BigDecimal amountUsd) {
        this.amountUsd = amountUsd;
    }

    public BigDecimal getExchangeRateUsed() {
        return exchangeRateUsed;
    }

    public void setExchangeRateUsed(BigDecimal exchangeRateUsed) {
        this.exchangeRateUsed = exchangeRateUsed;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    @PrePersist
    public void prePersist () {
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense expense)) return false;
        return id != null && id.equals(expense.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
