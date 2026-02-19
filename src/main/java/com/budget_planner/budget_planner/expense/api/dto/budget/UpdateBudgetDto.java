package com.budget_planner.budget_planner.expense.api.dto.budget;

import com.budget_planner.budget_planner.expense.domain.Period;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateBudgetDto(

        Period period,

        @Positive(message = "The amount value should be positive")
        @Digits(integer = 12, fraction = 2, message = "The integer part could be up to 12 digits, while the fraction part could be just two digits")
        BigDecimal amount,

        @FutureOrPresent(message = "The date could be only a future or present one")
        LocalDate startDate,

        UUID categoryId
) {}
