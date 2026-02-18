package com.budget_planner.budget_planner.expense.api.dto.category;

import com.budget_planner.budget_planner.expense.domain.Color;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryDto(
        @NotBlank(message = "The category name shouldn't be empty")
        @Size(max = 100, message = "The category name should be as much 100 characters")
        String name,

        Color color
) {}
