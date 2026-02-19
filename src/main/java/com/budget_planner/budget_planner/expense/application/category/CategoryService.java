package com.budget_planner.budget_planner.expense.application.category;

import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CreateCategoryDto;
import com.budget_planner.budget_planner.expense.api.dto.category.UpdateCategoryDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponseDto createCategory (CreateCategoryDto request);
    void deleteCategory (UUID id);
    List<CategoryResponseDto> getCategoriesByUser (UUID userId);
    CategoryResponseDto getCategoryById (UUID id);
    CategoryResponseDto updateCategory (UUID id, UpdateCategoryDto request);
}
