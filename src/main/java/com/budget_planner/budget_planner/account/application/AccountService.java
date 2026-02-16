package com.budget_planner.budget_planner.account.application;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.api.dto.CreateAccountDto;
import com.budget_planner.budget_planner.account.api.dto.UpdateAccountDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponseDto createAccount (CreateAccountDto request);
    void deleteAccount (UUID id);
    List<AccountResponseDto> getAccountsByUser (UUID userId);
    AccountResponseDto getAccountById (UUID id);
    AccountResponseDto updateAccount (UUID id, UpdateAccountDto request);
}
