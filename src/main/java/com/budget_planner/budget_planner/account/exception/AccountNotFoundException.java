package com.budget_planner.budget_planner.account.exception;

import com.budget_planner.budget_planner.common.exceptions.ResourceNotFoundException;

import java.util.UUID;

public class AccountNotFoundException extends ResourceNotFoundException {

    public AccountNotFoundException(UUID id) {
        super("Account with id " + id + " was not found");
    }
}
