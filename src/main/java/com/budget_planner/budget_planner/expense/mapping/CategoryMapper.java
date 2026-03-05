package com.budget_planner.budget_planner.expense.mapping;

import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CreateCategoryDto;
import com.budget_planner.budget_planner.expense.api.dto.category.UpdateCategoryDto;
import com.budget_planner.budget_planner.expense.domain.Category;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category createCategoryDtoToEntity (CreateCategoryDto request, User user) {
        return new Category(request.name(), request.color(), user);
    }

    public CategoryResponseDto categoryToResponseDto (Category category) {
        return new CategoryResponseDto(category.getId(), category.getName(), category.getColor().getHex(), category.getUser().getId());
    }

    public void merge (Category category, UpdateCategoryDto request) {
        if (request.name() != null && !request.name().isBlank())
            category.setName(request.name());

        if (request.color() != null)
            category.setColor(request.color());
    }
}
