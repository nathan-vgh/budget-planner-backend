package com.budget_planner.budget_planner.expense.application.expense;

import com.budget_planner.budget_planner.account.exception.AccountNotFoundException;
import com.budget_planner.budget_planner.account.persist.AccountRepository;
import com.budget_planner.budget_planner.expense.api.dto.expense.CreateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.ExpenseResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.UpdateExpenseDto;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
import com.budget_planner.budget_planner.expense.exception.ExpenseNotFoundException;
import com.budget_planner.budget_planner.expense.exception.TagNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.ExpenseMapper;
import com.budget_planner.budget_planner.expense.persist.CategoryRepository;
import com.budget_planner.budget_planner.expense.persist.ExpenseRepository;
import com.budget_planner.budget_planner.expense.persist.TagRepository;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl (ExpenseRepository expenseRepository, UserRepository userRepository, AccountRepository accountRepository, CategoryRepository categoryRepository, TagRepository tagRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public ExpenseResponseDto createExpense(CreateExpenseDto request) {
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        var account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));
        var category = categoryRepository.findById(request.accountId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
        var tags = new HashSet<>(tagRepository.findAllById(request.tagsId()));

        if (request.tagsId().size() != tags.size())
            throw new TagNotFoundException();

        var createdExpense = expenseMapper.createExpenseDtoToEntity(request, user, account, category, tags);

        // todo: In this part of the code, the usd amount should be calculated calling the exchange rate service.
        createdExpense.setAmountUsd(BigDecimal.ONE);
        createdExpense.setExchangeRateUsed(BigDecimal.ONE);

        return expenseMapper.expenseToResponseDto(createdExpense);
    }

    @Override
    public void deleteExpense(UUID id) {
        try {
            expenseRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException exception) {
            throw new ExpenseNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ExpenseResponseDto> getExpensesByUser(UUID id, Pageable pageable) {
        return expenseRepository.findAllByUserId(id, pageable)
                .map(expenseMapper::expenseToResponseDto);
    }

    @Transactional(readOnly = true)
    @Override
    public ExpenseResponseDto getExpenseById(UUID id) {
        var expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
        return expenseMapper.expenseToResponseDto(expense);
    }

    @Override
    public ExpenseResponseDto updateExpense(UUID id, UpdateExpenseDto request) {
        var expenseToBeUpdated = expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));

        var account = accountRepository.findById(request.accountId());
        var category = categoryRepository.findById(request.categoryId());
        var tags = new HashSet<>(tagRepository.findAllById(request.tagsId()));

        if (request.currency() != null)
            expenseToBeUpdated.setCurrency(request.currency());

        if (request.expenseDate() != null)
            expenseToBeUpdated.setExpenseDate(request.expenseDate());

        if (request.description() != null && !request.description().isBlank())
            expenseToBeUpdated.setDescription(request.description());

        if (!tags.isEmpty())
            expenseToBeUpdated.setTags(tags);

        if (request.amount() != null) {
            expenseToBeUpdated.setAmount(request.amount());
            // todo: here we need to recalculate the amount in usd and update the exchange rate used.
        }

        account.ifPresent(expenseToBeUpdated::setAccount);
        category.ifPresent(expenseToBeUpdated::setCategory);

        return expenseMapper.expenseToResponseDto(expenseToBeUpdated);
    }
}
