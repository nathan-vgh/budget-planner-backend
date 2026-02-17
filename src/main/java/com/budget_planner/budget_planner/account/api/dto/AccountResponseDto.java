package com.budget_planner.budget_planner.account.api.dto;

import com.budget_planner.budget_planner.account.domain.Type;

import java.util.UUID;

public record AccountResponseDto(UUID id, String name, Type type, UUID userId) {}
