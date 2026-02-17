package com.budget_planner.budget_planner.user.api.dto.user;

import com.budget_planner.budget_planner.user.api.dto.user_settings.UserSettingsResponseDto;

import java.util.UUID;

public record UserResponseDto (UUID id, String name, String email, UserSettingsResponseDto settings) {}
