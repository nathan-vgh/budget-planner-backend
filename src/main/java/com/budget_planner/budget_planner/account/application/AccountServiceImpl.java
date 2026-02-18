package com.budget_planner.budget_planner.account.application;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.api.dto.CreateAccountDto;
import com.budget_planner.budget_planner.account.api.dto.UpdateAccountDto;
import com.budget_planner.budget_planner.account.exception.AccountNotFoundException;
import com.budget_planner.budget_planner.account.mapping.AccountMapper;
import com.budget_planner.budget_planner.account.persist.AccountRepository;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    public AccountServiceImpl (AccountRepository accountRepository, UserRepository userRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountResponseDto createAccount(CreateAccountDto request) {
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        var createdAccount = accountRepository.save(accountMapper.createAccountDtoToEntity(request, user));
        return accountMapper.entityToResponseDto(createdAccount);
    }

    @Override
    public void deleteAccount(UUID id) {
        try {
            accountRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException exception) {
            throw new AccountNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountResponseDto> getAccountsByUser(UUID userId) {
        return accountRepository.findAllByUserId(userId)
                .stream()
                .map(accountMapper::entityToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public AccountResponseDto getAccountById(UUID id) {
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return accountMapper.entityToResponseDto(account);
    }

    @Override
    public AccountResponseDto updateAccount(UUID id, UpdateAccountDto request) {
        var accountToBeUpdated = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        if (request.name() != null && !request.name().isBlank())
            accountToBeUpdated.setName(request.name());

        if (request.type() != null)
            accountToBeUpdated.setType(request.type());

        return accountMapper.entityToResponseDto(accountToBeUpdated);
    }
}
