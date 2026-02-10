package com.budget_planner.budget_planner.user.api;

import com.budget_planner.budget_planner.user.api.dto.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.UserResponseDto;
import com.budget_planner.budget_planner.user.application.UserService;
import com.budget_planner.budget_planner.user.mapping.UserMapping;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final UserMapping userMapping;

    public UserController (UserService service, UserMapping userMapping) {
        this.service = service;
        this.userMapping = userMapping;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser (@Valid @RequestBody CreateUserDto userDto) {
        var createdUser = service.createUser(userMapping.createRequestDtoToUser(userDto));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(location).body(userMapping.userToResponseDto(createdUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser (@PathVariable UUID id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers (@PageableDefault(size = 2) Pageable pageable) {
        var users = service.getAllUsers(pageable).map(userMapping::userToResponseDto);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById (@PathVariable UUID id) {
        var user = service.getUserById(id);
        return ResponseEntity.ok(userMapping.userToResponseDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser (@PathVariable UUID id, @Valid @RequestBody UpdateUserDto userDto) {
        var updatedUser = service.updateUser(id, userMapping.updateRequestDtoToUser(userDto));
        return ResponseEntity.ok(userMapping.userToResponseDto(updatedUser));
    }
}
