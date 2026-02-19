package com.budget_planner.budget_planner.expense.api.dto.expense;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

public record CreateExpenseDto(

        @NotNull(message = "The amount shouldn't be null")
        @Positive(message = "The amount value should be positive")
        @Digits(integer = 17, fraction = 2, message = "The integer part could be up to 17 digits, while the fraction part could be just two digits")
        BigDecimal amount,

        @NotNull(message = "The currency shouldn't be null")
        Currency currency,

        @NotNull(message = "The expense date shouldn't be null")
        @FutureOrPresent(message = "The date could be only a future or present one")
        LocalDate expenseDate,

        @NotNull(message = "The description shouldn't be null")
        @NotBlank(message = "The description shouldn't be empty")
        @Size(max = 255, message = "The description should be as much 255 characters")
        String description,

        @NotNull(message = "The user id shouldn't be null")
        UUID userId,

        @NotNull(message = "The account id shouldn't be null")
        UUID accountId,

        @NotNull(message = "The category id shouldn't be null")
        UUID categoryId,

        Set<UUID> tagsId
) {}
