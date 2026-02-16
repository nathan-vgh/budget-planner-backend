package com.budget_planner.budget_planner.account.api.dto;

import com.budget_planner.budget_planner.account.domain.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateAccountDto(

        @NotBlank(message = "The account name shouldn't be empty")
        @Size(max = 100, message = "The account name should be as much 100 characters")
        String name,

        @Enumerated(EnumType.STRING)
        AccountType type,

        @NotNull(message = "The user id shouldn't be null")
        UUID userId
) {}
