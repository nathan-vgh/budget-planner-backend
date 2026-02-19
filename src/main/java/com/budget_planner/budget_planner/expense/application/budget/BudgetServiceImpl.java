package com.budget_planner.budget_planner.expense.application.budget;

import com.budget_planner.budget_planner.expense.api.dto.budget.BudgetResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.CreateBudgetDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.UpdateBudgetDto;
import com.budget_planner.budget_planner.expense.exception.BudgetNotFoundException;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.BudgetMapper;
import com.budget_planner.budget_planner.expense.persist.BudgetRepository;
import com.budget_planner.budget_planner.expense.persist.CategoryRepository;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;

    public BudgetServiceImpl (BudgetRepository budgetRepository, CategoryRepository categoryRepository, UserRepository userRepository, BudgetMapper budgetMapper) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.budgetMapper = budgetMapper;
    }

    @Override
    public BudgetResponseDto createBudget(CreateBudgetDto request) {
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
        var createdBudget = budgetRepository.save(budgetMapper.createBudgetDtoToEntity(request, user, category));
        return budgetMapper.budgetToResponseDto(createdBudget);
    }

    @Override
    public void deleteBudget(UUID id) {
        try {
            budgetRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException exception) {
            throw new BudgetNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Page<BudgetResponseDto> getBudgetsByUser(UUID id, Pageable pageable) {
        return budgetRepository.findAllByUserId(id, pageable)
                .map(budgetMapper::budgetToResponseDto);
    }

    @Transactional(readOnly = true)
    @Override
    public BudgetResponseDto getBudgetById(UUID id) {
        var budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));
        return budgetMapper.budgetToResponseDto(budget);
    }

    @Override
    public BudgetResponseDto updateBudget(UUID id, UpdateBudgetDto request) {
        var budgetToBeUpdated = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));

        var newCategory = categoryRepository.findById(request.categoryId());

        if (request.period() != null)
            budgetToBeUpdated.setPeriod(request.period());

        if (request.amount() != null)
            budgetToBeUpdated.setAmount(request.amount());

        if (request.startDate() != null)
            budgetToBeUpdated.setStartDate(request.startDate());

        newCategory.ifPresent(budgetToBeUpdated::setCategory);

        return budgetMapper.budgetToResponseDto(budgetToBeUpdated);
    }
}
