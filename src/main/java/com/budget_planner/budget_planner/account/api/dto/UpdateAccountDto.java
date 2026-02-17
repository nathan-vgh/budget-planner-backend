package com.budget_planner.budget_planner.account.api.dto;

import com.budget_planner.budget_planner.account.domain.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAccountDto(

        @NotBlank(message = "The account name shouldn't be empty")
        @Size(max = 100, message = "The account name should be as much 100 characters")
        String name,

        Type type
) {}
