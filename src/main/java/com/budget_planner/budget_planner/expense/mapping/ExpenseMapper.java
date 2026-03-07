package com.budget_planner.budget_planner.expense.mapping;

import com.budget_planner.budget_planner.account.domain.Account;
import com.budget_planner.budget_planner.account.mapping.AccountMapper;
import com.budget_planner.budget_planner.expense.api.dto.expense.CreateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.ExpenseResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.UpdateExpenseDto;
import com.budget_planner.budget_planner.expense.domain.Category;
import com.budget_planner.budget_planner.expense.domain.Expense;
import com.budget_planner.budget_planner.expense.domain.Tag;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
        var expense =  new Expense(
                request.amount(),
                request.currency(),
                request.expenseDate(),
                request.description(),
                user,
                account,
                category,
                tags
        );

        // todo: In this part of the code, the usd amount should be calculated calling the exchange rate service.
        expense.setAmountUsd(BigDecimal.ONE);
        expense.setExchangeRateUsed(BigDecimal.ONE);

        return expense;
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

    public void merge (Expense expense, UpdateExpenseDto request, Account account, Category category, Set<Tag> tags) {
        if (request.currency() != null)
            expense.setCurrency(request.currency());

        if (request.expenseDate() != null)
            expense.setExpenseDate(request.expenseDate());

        if (request.description() != null && !request.description().isBlank())
            expense.setDescription(request.description());

        if (account != null)
            expense.setAccount(account);

        if (category != null)
            expense.setCategory(category);

        if (!tags.isEmpty())
            expense.setTags(tags);

        if (request.amount() != null) {
            expense.setAmount(request.amount());
            // todo: here we need to recalculate the amount in usd and update the exchange rate used.
        }
    }
}