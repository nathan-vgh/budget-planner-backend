package com.budget_planner.budget_planner.user.api;

import com.budget_planner.budget_planner.user.api.dto.user.CreateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UpdateUserDto;
import com.budget_planner.budget_planner.user.api.dto.user.UserResponseDto;
import com.budget_planner.budget_planner.user.api.dto.user_settings.UserSettingsResponseDto;
import com.budget_planner.budget_planner.user.application.UserService;
import com.budget_planner.budget_planner.user.domain.UserSettings;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User controller unit tests")
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    private UUID userId;

    private CreateUserDto createRequest;
    private UpdateUserDto updateRequest;
    private UserResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();

        var settings = new UserSettings();
        var userSettings = new UserSettingsResponseDto(settings.getCurrency().getCurrencyCode(), settings.getWeekStartDay(), settings.getLanguage().toLanguageTag(), settings.getTheme());

        this.createRequest = new CreateUserDto("TestUser", "user@test.com", "test123");
        this.updateRequest = new UpdateUserDto("TestUserUpdated", null, null);
        this.response = new UserResponseDto(userId, "TestUser", "user@test.com", userSettings);
    }

    @Nested
    @DisplayName("Create user tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully () throws Exception {
            when(service.createUser(createRequest))
                    .thenReturn(response);

            var expectedLocation = "/api/v1/users/" + response.id();

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(expectedLocation)))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.settings.currency").value(response.settings().currency()))
                    .andExpect(jsonPath("$.settings.weekStartDay").value(response.settings().weekStartDay().toString()))
                    .andExpect(jsonPath("$.settings.language").value(response.settings().language()))
                    .andExpect(jsonPath("$.settings.theme").value(response.settings().theme().toString()));

            verify(service, times(1)).createUser(createRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badCreateRequest = new CreateUserDto(null, null, null);

            mockMvc.perform(post("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badCreateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Delete user tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully () throws Exception {
            doNothing().when(service)
                    .deleteUser(userId);

            mockMvc.perform(delete("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deleteUser(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .deleteUser(userId);

            mockMvc.perform(delete("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).deleteUser(userId);
        }
    }

    @Nested
    @DisplayName("Get user by id tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should get user successfully")
        void shouldGetUserSuccessfully () throws Exception {
            when(service.getUserById(userId))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.settings.currency").value(response.settings().currency()))
                    .andExpect(jsonPath("$.settings.weekStartDay").value(response.settings().weekStartDay().toString()))
                    .andExpect(jsonPath("$.settings.language").value(response.settings().language()))
                    .andExpect(jsonPath("$.settings.theme").value(response.settings().theme().toString()));

            verify(service, times(1)).getUserById(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .getUserById(userId);

            mockMvc.perform(get("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getUserById(userId);
        }
    }

    @Nested
    @DisplayName("Update user tests")
    class UpdateUserTests {

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully () throws Exception {
            when(service.updateUser(userId, updateRequest))
                    .thenReturn(response);

            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.email").value(response.email()))
                    .andExpect(jsonPath("$.settings.currency").value(response.settings().currency()))
                    .andExpect(jsonPath("$.settings.weekStartDay").value(response.settings().weekStartDay().toString()))
                    .andExpect(jsonPath("$.settings.language").value(response.settings().language()))
                    .andExpect(jsonPath("$.settings.theme").value(response.settings().theme().toString()));

            verify(service, times(1)).updateUser(userId, updateRequest);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .updateUser(userId, updateRequest);

            mockMvc.perform(put("/api/v1/users/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).updateUser(userId, updateRequest);
        }
    }
}