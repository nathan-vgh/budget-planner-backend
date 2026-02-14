package com.budget_planner.budget_planner.user.application;

import com.budget_planner.budget_planner.user.api.dto.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(CreateUserDto request);
    void deleteUser(UUID id);
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(UUID id);
    UserResponseDto updateUser(UUID id, UpdateUserDto request);
}
