package com.budget_planner.budget_planner.expense.api.dto.expense;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

public record UpdateExpenseDto(

        @Positive(message = "The amount value should be positive")
        @Digits(integer = 17, fraction = 2, message = "The integer part could be up to 17 digits, while the fraction part could be just two digits")
        BigDecimal amount,

        Currency currency,

        @PastOrPresent(message = "The date could be only a past or present one")
        LocalDate expenseDate,

        @NotBlank(message = "The description shouldn't be empty")
        @Size(max = 255, message = "The description should be as much 255 characters")
        String description,

        UUID accountId,

        UUID categoryId,

        Set<UUID> tagsId
) {}
