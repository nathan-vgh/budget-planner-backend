package com.budget_planner.budget_planner.user.application;

import com.budget_planner.budget_planner.user.api.dto.user.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UserResponseDto;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.mapping.UserMapper;
import com.budget_planner.budget_planner.user.persist.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Locale;
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
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::userToResponseDto);
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

        if (request.name() != null && !request.name().isBlank())
            userToBeUpdated.setName(request.name());

        if (request.email() != null && !request.email().isBlank())
            userToBeUpdated.setEmail(request.email());

        if (request.settings() != null) {
            var settingsDto = request.settings();

            var currency = settingsDto.currency() != null
                    ? Currency.getInstance(settingsDto.currency())
                    : null;

            var locale = settingsDto.language() != null
                    ? Locale.forLanguageTag(settingsDto.language())
                    : null;

            userToBeUpdated.setSettings(
                    currency,
                    settingsDto.weekStartDay(),
                    locale,
                    settingsDto.theme()
            );
        }

        return userMapper.userToResponseDto(userToBeUpdated);
    }
}
