package com.budget_planner.budget_planner.expense.application;

import com.budget_planner.budget_planner.expense.api.dto.budget.BudgetResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.CreateBudgetDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.UpdateBudgetDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BudgetService {
    BudgetResponseDto createBudget (CreateBudgetDto request);
    void deleteBudget (UUID id);
    Page<BudgetResponseDto> getBudgetsByUser (UUID id, Pageable pageable);
    BudgetResponseDto getBudgetById (UUID id);
    BudgetResponseDto updateBudget (UUID id, UpdateBudgetDto request);
}
