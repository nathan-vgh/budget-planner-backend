package com.budget_planner.budget_planner.expense.api.dto.budget;

import com.budget_planner.budget_planner.expense.domain.Period;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBudgetDto(

    @NotNull(message = "The period shouldn't be null")
    Period period,

    @NotNull(message = "The amount shouldn't be null")
    @DecimalMin("0.01")
    @Digits(integer = 12, fraction = 2, message = "The integer part could be up to 12 digits, while the fraction part could be just two digits")
    BigDecimal amount,

    @NotNull(message = "The start date shouldn't be null")
    @FutureOrPresent
    LocalDate startDate,

    @NotNull(message = "The user id shouldn't be null")
    UUID userId,

    @NotNull(message = "The category id shouldn't be null")
    UUID categoryId
) {}
