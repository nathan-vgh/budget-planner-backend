package com.budget_planner.budget_planner.account.api.dto;

import com.budget_planner.budget_planner.account.domain.AccountType;

import java.util.UUID;

public record AccountResponseDto(UUID id, String name, AccountType type, UUID userId) {}
