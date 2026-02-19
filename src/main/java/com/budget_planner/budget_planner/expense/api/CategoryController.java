package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CreateCategoryDto;
import com.budget_planner.budget_planner.expense.api.dto.category.UpdateCategoryDto;
import com.budget_planner.budget_planner.expense.application.category.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController (CategoryService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory (@Valid @RequestBody CreateCategoryDto request) {
        var createdCategory = service.createCategory(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.id())
                .toUri();

        return ResponseEntity.created(location).body(createdCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory (@PathVariable UUID id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<CategoryResponseDto>> getCategoriesByUser (@PathVariable UUID id) {
        var categories = service.getCategoriesByUser(id);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById (@PathVariable UUID id) {
        var category = service.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory (@PathVariable UUID id, @Valid @RequestBody UpdateCategoryDto request) {
        var updatedCategory = service.updateCategory(id, request);
        return ResponseEntity.ok(updatedCategory);
    }
}
