package com.budget_planner.budget_planner.expense.exception;

import com.budget_planner.budget_planner.common.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class CategoryNotFoundException extends ResourceNotFoundException {

    public CategoryNotFoundException(UUID id) { super("Category with id " + id + " was not found"); }
}
