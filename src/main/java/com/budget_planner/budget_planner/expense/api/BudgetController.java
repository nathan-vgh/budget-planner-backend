package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.expense.api.dto.budget.BudgetResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.CreateBudgetDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.UpdateBudgetDto;
import com.budget_planner.budget_planner.expense.application.budget.BudgetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private final BudgetService service;

    public BudgetController (BudgetService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<BudgetResponseDto> createBudget (@Valid @RequestBody CreateBudgetDto request) {
        var createdBudget = service.createBudget(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBudget.id())
                .toUri();

        return ResponseEntity.created(location).body(createdBudget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget (@PathVariable UUID id) {
        service.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<BudgetResponseDto>> getBudgetsByUser (@PathVariable UUID id, Pageable pageable) {
        var budgets = service.getBudgetsByUser(id, pageable);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> getBudgetById (@PathVariable UUID id) {
        var budget = service.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDto> updateBudget (@PathVariable UUID id, @Valid @RequestBody UpdateBudgetDto request) {
        var updatedBudget = service.updateBudget(id, request);
        return ResponseEntity.ok(updatedBudget);
    }
}
