package com.budget_planner.budget_planner.user.mapping;

import com.budget_planner.budget_planner.user.api.dto.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.UserResponseDto;
import com.budget_planner.budget_planner.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapping {

    public User createRequestDtoToUser(CreateUserDto dto) {
        return new User(dto.name(), dto.email(), dto.password());
    }

    public User updateRequestDtoToUser(UpdateUserDto dto) {
        return new User(dto.name(), dto.email());
    }

    public UserResponseDto userToResponseDto (User user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }
}
