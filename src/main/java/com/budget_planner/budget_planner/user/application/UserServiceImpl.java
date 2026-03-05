package com.budget_planner.budget_planner.user.application;

import com.budget_planner.budget_planner.user.api.dto.user.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UserResponseDto;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.mapping.UserMapper;
import com.budget_planner.budget_planner.user.persist.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl (UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto createUser(CreateUserDto request) {
        var createdUser = userRepository.save(userMapper.createRequestDtoToUser(request));
        return userMapper.userToResponseDto(createdUser);
    }

    @Override
    public void deleteUser(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.userToResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(UUID id, UpdateUserDto request) {
        var userToBeUpdated = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.merge(userToBeUpdated, request);

        return userMapper.userToResponseDto(userToBeUpdated);
    }
}
