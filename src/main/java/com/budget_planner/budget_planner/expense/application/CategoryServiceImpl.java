package com.budget_planner.budget_planner.expense.application;

import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CreateCategoryDto;
import com.budget_planner.budget_planner.expense.api.dto.category.UpdateCategoryDto;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.CategoryMapper;
import com.budget_planner.budget_planner.expense.persist.CategoryRepository;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    CategoryServiceImpl (CategoryRepository categoryRepository, UserRepository userRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryResponseDto createCategory(CreateCategoryDto request) {
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        var createdCategory = categoryRepository.save(categoryMapper.createCategoryDtoToEntity(request, user));
        return categoryMapper.categoryToResponseDto(createdCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        try {
            categoryRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException exception) {
            throw new CategoryNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponseDto> getCategoriesByUser(UUID userId) {
        return categoryRepository.findAllByUserId(userId)
                .stream()
                .map(categoryMapper::categoryToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponseDto getCategoryById(UUID id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.categoryToResponseDto(category);
    }

    @Override
    public CategoryResponseDto updateCategory(UUID id, UpdateCategoryDto request) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (request.name() != null && !request.name().isBlank())
            category.setName(request.name());

        if (request.color() != null)
            category.setColor(request.color());

        return categoryMapper.categoryToResponseDto(category);
    }
}
