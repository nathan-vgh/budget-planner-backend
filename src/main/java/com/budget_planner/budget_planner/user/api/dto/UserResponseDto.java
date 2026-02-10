package com.budget_planner.budget_planner.user.api.dto;

import java.util.UUID;

public record UserResponseDto (UUID id, String name, String email) {}
