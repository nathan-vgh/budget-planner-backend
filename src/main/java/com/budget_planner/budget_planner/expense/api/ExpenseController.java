package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.expense.api.dto.expense.CreateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.ExpenseResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.UpdateExpenseDto;
import com.budget_planner.budget_planner.expense.application.expense.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController (ExpenseService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ExpenseResponseDto> createExpense (@Valid @RequestBody CreateExpenseDto request) {
        var createdExpense = service.createExpense(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdExpense.id())
                .toUri();

        return ResponseEntity.created(location).body(createdExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense (@PathVariable UUID id) {
        service.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<ExpenseResponseDto>> getExpensesByUser (@PathVariable UUID id, Pageable pageable) {
        var expenses = service.getExpensesByUser(id, pageable);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById (@PathVariable UUID id) {
        var expense = service.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> updateExpense (@PathVariable UUID id, @Valid @RequestBody UpdateExpenseDto request) {
        var updatedExpense = service.updateExpense(id, request);
        return ResponseEntity.ok(updatedExpense);
    }
}
