package com.budget_planner.budget_planner.expense.mapping;

import com.budget_planner.budget_planner.account.domain.Account;
import com.budget_planner.budget_planner.account.mapping.AccountMapper;
import com.budget_planner.budget_planner.expense.api.dto.expense.CreateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.ExpenseResponseDto;
import com.budget_planner.budget_planner.expense.domain.Category;
import com.budget_planner.budget_planner.expense.domain.Expense;
import com.budget_planner.budget_planner.expense.domain.Tag;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ExpenseMapper {

    private final AccountMapper accountMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    public ExpenseMapper (AccountMapper accountMapper, CategoryMapper categoryMapper, TagMapper tagMapper) {
        this.accountMapper = accountMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    public Expense createExpenseDtoToEntity (CreateExpenseDto request, User user, Account account, Category category, Set<Tag> tags) {
        return new Expense(
                request.amount(),
                request.currency(),
                request.expenseDate(),
                request.description(),
                user,
                account,
                category,
                tags
        );
    }

    public ExpenseResponseDto expenseToResponseDto (Expense expense) {
        return new ExpenseResponseDto(
                expense.getId(),
                expense.getAmount(),
                expense.getCurrency(),
                expense.getAmountUsd(),
                expense.getExchangeRateUsed(),
                expense.getExpenseDate(),
                expense.getDescription(),
                expense.getUser().getId(),
                accountMapper.entityToResponseDto(expense.getAccount()),
                categoryMapper.categoryToResponseDto(expense.getCategory()),
                expense.getTags().stream().map(tagMapper::tagToResponseDto).collect(Collectors.toSet())
        );
    }
}