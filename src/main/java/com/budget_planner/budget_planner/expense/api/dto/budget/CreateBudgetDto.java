package com.budget_planner.budget_planner.expense.api.dto.budget;

import com.budget_planner.budget_planner.expense.domain.Period;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBudgetDto(

    @NotNull(message = "The period shouldn't be null")
    Period period,

    @NotNull(message = "The amount shouldn't be null")
    @Positive(message = "The amount value should be positive")
    @Digits(integer = 12, fraction = 2, message = "The integer part could be up to 12 digits, while the fraction part could be just two digits")
    BigDecimal amount,

    @NotNull(message = "The start date shouldn't be null")
    @FutureOrPresent(message = "The date could be only a future or present one")
    LocalDate startDate,

    @NotNull(message = "The user id shouldn't be null")
    UUID userId,

    @NotNull(message = "The category id shouldn't be null")
    UUID categoryId
) {}
