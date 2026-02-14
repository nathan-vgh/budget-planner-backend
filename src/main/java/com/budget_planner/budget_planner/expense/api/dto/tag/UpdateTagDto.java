package com.budget_planner.budget_planner.expense.api.dto.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTagDto(

        @NotBlank(message = "The tag name shouldn't be empty")
        @Size(max = 50, message = "The tag name should be as much 50 characters")
        String name
) {}
