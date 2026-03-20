package com.budget_planner.budget_planner.user.application;

import com.budget_planner.budget_planner.user.api.dto.user.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UserResponseDto;
import com.budget_planner.budget_planner.user.api.dto.user_settings.UserSettingsResponseDto;
import com.budget_planner.budget_planner.user.domain.Theme;
import com.budget_planner.budget_planner.user.domain.User;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.mapping.UserMapper;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User service unit tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl service;

    private UUID userId;
    private User user;

    private CreateUserDto createRequest;
    private UpdateUserDto updateRequest;
    private UserResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.user = new User("TestUser", "test@domain", "Test123.");

        var settings = new UserSettingsResponseDto("USD", DayOfWeek.MONDAY, "en-us", Theme.SYSTEM);

        this.createRequest = new CreateUserDto("TestUser", "test@domain.com", "Test123.");
        this.updateRequest = new UpdateUserDto("TestUserUpdated", null, null);
        this.response = new UserResponseDto(userId, "TestUser", "test@domain.com", settings);
    }

    @Nested
    @DisplayName("Create user tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully () {
            when(userMapper.createRequestDtoToUser(createRequest))
                    .thenReturn(user);

            when(userRepository.save(any(User.class)))
                    .thenReturn(user);

            when(userMapper.userToResponseDto(user))
                    .thenReturn(response);

            var createdUser = service.createUser(createRequest);

            assertNotNull(createdUser);
            assertEquals(createdUser, response);

            verify(userMapper, times(1)).createRequestDtoToUser(createRequest);
            verify(userRepository, times(1)).save(any(User.class));
            verify(userMapper, times(1)).userToResponseDto(user);
        }
    }

    @Nested
    @DisplayName("Delete user tests")
    class UserDeleteTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            service.deleteUser(userId);

            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).delete(user);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.deleteUser(userId));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(0)).delete(user);
        }
    }

    @Nested
    @DisplayName("Get user by id tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should get user successfully")
        void shouldGetUserSuccessfully () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(userMapper.userToResponseDto(user))
                    .thenReturn(response);

            var gotUser = service.getUserById(userId);

            assertNotNull(gotUser);
            assertEquals(gotUser, response);

            verify(userRepository, times(1)).findById(userId);
            verify(userMapper, times(1)).userToResponseDto(user);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.getUserById(userId));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verifyNoInteractions(userMapper);
        }
    }

    @Nested
    @DisplayName("Update user tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(userMapper.userToResponseDto(user))
                    .thenReturn(response);

            var updateUser = service.updateUser(userId, updateRequest);

            assertNotNull(updateUser);
            assertEquals(updateUser, response);

            verify(userRepository, times(1)).findById(userId);
            verify(userMapper, times(1)).merge(user, updateRequest);
            verify(userMapper, times(1)).userToResponseDto(user);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.updateUser(userId, updateRequest));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verifyNoInteractions(userMapper);
        }
    }
}