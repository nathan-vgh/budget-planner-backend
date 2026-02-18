package com.budget_planner.budget_planner.expense.api.dto.budget;

import com.budget_planner.budget_planner.expense.domain.Period;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateBudgetDto(

        Period period,

        @DecimalMin("0.01")
        @Digits(integer = 12, fraction = 2, message = "The integer part could be up to 12 digits, while the fraction part could be just two digits")
        BigDecimal amount,

        @FutureOrPresent
        LocalDate startDate,

        UUID categoryId
) {}
