package com.budget_planner.budget_planner.expense.mapping;

import com.budget_planner.budget_planner.expense.api.dto.budget.BudgetResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.CreateBudgetDto;
import com.budget_planner.budget_planner.expense.domain.Budget;
import com.budget_planner.budget_planner.expense.domain.Category;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

    private final CategoryMapper categoryMapper;

    public BudgetMapper (CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public Budget createBudgetDtoToEntity (CreateBudgetDto request, User user, Category category) {
        return new Budget(request.period(), request.amount(), request.startDate(), user, category);
    }

    public BudgetResponseDto budgetToResponseDto (Budget budget) {
        return new BudgetResponseDto(budget.getId(), budget.getPeriod(), budget.getAmount(), budget.getStartDate(), budget.getEndDate(), budget.getUser().getId(), categoryMapper.categoryToResponseDto(budget.getCategory()));
    }
}
