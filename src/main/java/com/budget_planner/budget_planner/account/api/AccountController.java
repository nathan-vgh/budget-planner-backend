package com.budget_planner.budget_planner.account.api;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.api.dto.CreateAccountDto;
import com.budget_planner.budget_planner.account.api.dto.UpdateAccountDto;
import com.budget_planner.budget_planner.account.application.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    AccountController (AccountService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount (@Valid @RequestBody CreateAccountDto request) {
        var createdAccount = service.createAccount(request);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccount.id())
                .toUri();

        return ResponseEntity.created(location).body(createdAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount (@PathVariable UUID id) {
        service.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByUser (@PathVariable UUID id) {
        var accounts = service.getAccountsByUser(id);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById (@PathVariable UUID id) {
        var account = service.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> updateAccount (@PathVariable UUID id, @Valid @RequestBody UpdateAccountDto request) {
        var updatedAccount = service.updateAccount(id, request);
        return ResponseEntity.ok(updatedAccount);
    }
}
