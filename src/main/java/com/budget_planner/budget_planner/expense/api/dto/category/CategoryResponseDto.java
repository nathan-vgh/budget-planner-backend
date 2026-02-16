package com.budget_planner.budget_planner.expense.api.dto.category;

import java.util.UUID;

public record CategoryResponseDto(UUID id, String name, String color, UUID userId) {}
