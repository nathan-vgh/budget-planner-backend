package com.budget_planner.budget_planner.expense.api.dto.budget;

import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.domain.Period;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BudgetResponseDto(UUID id, Period period, BigDecimal amount, LocalDate startDate, LocalDate endDate, UUID userId, CategoryResponseDto category) {}
