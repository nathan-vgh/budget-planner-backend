package com.budget_planner.budget_planner.user.api.dto.user;

import com.budget_planner.budget_planner.user.api.dto.user_settings.UpdateUserSettingsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserDto (

        @Size(max = 100, message = "The user name should be as much 100 characters")
        String name,

        @Email(message = "The user email should follow the email format")
        String email,

        @Valid
        UpdateUserSettingsDto settings
) {}
