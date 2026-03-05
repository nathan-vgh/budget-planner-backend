package com.budget_planner.budget_planner.user.application;

import com.budget_planner.budget_planner.user.api.dto.user.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UserResponseDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(CreateUserDto request);
    void deleteUser(UUID id);
    UserResponseDto getUserById(UUID id);
    UserResponseDto updateUser(UUID id, UpdateUserDto request);
}
