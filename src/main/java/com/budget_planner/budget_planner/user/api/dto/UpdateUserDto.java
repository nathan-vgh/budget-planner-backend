package com.budget_planner.budget_planner.user.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserDto (

        @Size(max = 100, message = "The user name should be as much 100 characters")
        String name,

        @Email(message = "The user email should follow the email format")
        String email
) {}
