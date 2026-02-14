package com.budget_planner.budget_planner.expense.api.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTagDto(

        @NotBlank(message = "The tag name shouldn't be empty")
        @Size(max = 50, message = "The tag name should be as much 50 characters")
        String name,

        @NotNull(message = "The user id shouldn't be null")
        UUID userId
) {}
