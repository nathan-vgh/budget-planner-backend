package com.budget_planner.budget_planner.expense.exception;

import com.budget_planner.budget_planner.common.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class ExpenseNotFoundException extends ResourceNotFoundException {

    public ExpenseNotFoundException(UUID id) { super("Expense with id " + id + " was not found"); }
}
