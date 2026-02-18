package com.budget_planner.budget_planner.expense.exception;

import com.budget_planner.budget_planner.common.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class BudgetNotFoundException extends ResourceNotFoundException {

    public BudgetNotFoundException(UUID id) {
        super("Budget with id " + id + " was not found");
    }
}
