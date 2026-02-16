package com.budget_planner.budget_planner.expense.exception;

import com.budget_planner.budget_planner.common.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class TagNotFoundException extends ResourceNotFoundException {

    public TagNotFoundException(UUID id) { super("Tag with id " + id + " was not found"); }
}
