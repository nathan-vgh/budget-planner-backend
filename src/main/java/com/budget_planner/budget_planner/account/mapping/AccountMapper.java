package com.budget_planner.budget_planner.account.mapping;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.api.dto.CreateAccountDto;
import com.budget_planner.budget_planner.account.domain.Account;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account createAccountDtoToEntity (CreateAccountDto request, User user) {
        return new Account(request.name(), request.type(), user);
    }

    public AccountResponseDto entityToResponseDto (Account account) {
        return new AccountResponseDto(account.getId(), account.getName(), account.getType(), account.getUser().getId());
    }
}
