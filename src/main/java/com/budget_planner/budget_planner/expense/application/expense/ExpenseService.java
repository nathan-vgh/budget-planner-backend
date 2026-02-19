package com.budget_planner.budget_planner.expense.application.expense;

import com.budget_planner.budget_planner.expense.api.dto.expense.CreateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.ExpenseResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.UpdateExpenseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ExpenseService {
    ExpenseResponseDto createExpense (CreateExpenseDto request);
    void deleteExpense (UUID id);
    Page<ExpenseResponseDto> getExpensesByUser (UUID id, Pageable pageable);
    ExpenseResponseDto getExpenseById (UUID id);
    ExpenseResponseDto updateExpense (UUID id, UpdateExpenseDto request);
}
