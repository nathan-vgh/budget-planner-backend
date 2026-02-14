package com.budget_planner.budget_planner.expense.api.dto.tag;

import java.util.UUID;

public record TagResponseDto(UUID id, String name, UUID userId) {}
