package com.budget_planner.budget_planner.expense.api.dto.category;

import com.budget_planner.budget_planner.expense.domain.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCategoryDto(

        @NotBlank(message = "The category name shouldn't be empty")
        @Size(max = 100, message = "The category name should be as much 100 characters")
        String name,

        Color color,

        @NotNull(message = "The user id shouldn't be null")
        UUID userId
) {}
