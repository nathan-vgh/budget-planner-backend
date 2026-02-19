package com.budget_planner.budget_planner.expense.api.dto.expense;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

public record ExpenseResponseDto(
        UUID id,
        BigDecimal amount,
        Currency currency,
        BigDecimal amountUsd,
        BigDecimal exchangeRateUsed,
        LocalDate expenseDate,
        String description,
        UUID userId,
        AccountResponseDto account,
        CategoryResponseDto category,
        Set<TagResponseDto> tags
) {}
