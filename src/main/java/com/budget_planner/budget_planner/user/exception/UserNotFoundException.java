package com.budget_planner.budget_planner.user.exception;

import com.budget_planner.budget_planner.common.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException (UUID id) {
        super("User with id " + id + " was not found");
    }
}
