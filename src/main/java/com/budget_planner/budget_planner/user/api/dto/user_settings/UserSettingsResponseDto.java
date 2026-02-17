package com.budget_planner.budget_planner.user.api.dto.user_settings;

import com.budget_planner.budget_planner.user.domain.Theme;

import java.time.DayOfWeek;

public record UserSettingsResponseDto (String currency, DayOfWeek weekStartDay, String language, Theme theme) {}
