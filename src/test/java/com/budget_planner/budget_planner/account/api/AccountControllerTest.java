package com.budget_planner.budget_planner.account.api;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.api.dto.CreateAccountDto;
import com.budget_planner.budget_planner.account.api.dto.UpdateAccountDto;
import com.budget_planner.budget_planner.account.application.AccountService;
import com.budget_planner.budget_planner.account.domain.Type;
import com.budget_planner.budget_planner.account.exception.AccountNotFoundException;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@DisplayName("Account controller unit tests")
class AccountControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService service;

    private UUID userId;
    private UUID accountId;

    private CreateAccountDto createRequest;
    private UpdateAccountDto updateRequest;
    private AccountResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.accountId = UUID.randomUUID();

        this.createRequest = new CreateAccountDto("TestAccount", Type.CASH, userId);
        this.updateRequest = new UpdateAccountDto("TestAccountUpdated", null);
        this.response = new AccountResponseDto(accountId, "TestAccount", Type.CASH, userId);
    }

    @Nested
    @DisplayName("Create account tests")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully")
        void shouldCreateAccountSuccessfully () throws Exception {
            when(service.createAccount(createRequest))
                    .thenReturn(response);

            var expectedLocation = "/api/v1/accounts/" + response.id();

            mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(expectedLocation)))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.type").value(response.type().toString()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).createAccount(createRequest);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .createAccount(createRequest);

            mockMvc.perform(post("/api/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createAccount(createRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badCreateRequest = new CreateAccountDto(null, null, null);

            mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badCreateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Delete account tests")
    class DeleteAccountTests {

        @Test
        @DisplayName("Should delete account successfully")
        void shouldDeleteAccountSuccessfully () throws Exception {
            doNothing().when(service)
                    .deleteAccount(accountId);

            mockMvc.perform(delete("/api/v1/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deleteAccount(accountId);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () throws Exception {
            doThrow(AccountNotFoundException.class)
                    .when(service)
                    .deleteAccount(accountId);

            mockMvc.perform(delete("/api/v1/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).deleteAccount(accountId);
        }
    }

    @Nested
    @DisplayName("Get accounts by user id tests")
    class GetAccountsByUserTests {

        @Test
        @DisplayName("Should get accounts list")
        void shouldGetAccountsList () throws Exception {
            when(service.getAccountsByUser(userId))
                    .thenReturn(List.of(response));

            mockMvc.perform(get("/api/v1/accounts/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(service, times(1)).getAccountsByUser(userId);
        }

        @Test
        @DisplayName("Should get empty accounts list")
        void shouldGetEmptyAccountsList () throws Exception {
            when(service.getAccountsByUser(userId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/v1/accounts/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(service, times(1)).getAccountsByUser(userId);
        }
    }

    @Nested
    @DisplayName("Get account by id tests")
    class GetAccountByIdTests {

        @Test
        @DisplayName("Should get account successfully")
        void shouldGetAccountSuccessfully () throws Exception {
            when(service.getAccountById(accountId))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.type").value(response.type().toString()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).getAccountById(accountId);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () throws Exception {
            doThrow(AccountNotFoundException.class)
                    .when(service)
                    .getAccountById(accountId);

            mockMvc.perform(get("/api/v1/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getAccountById(accountId);
        }
    }

    @Nested
    @DisplayName("Update account tests")
    class UpdateAccountTests {

        @Test
        @DisplayName("Should update account successfully")
        void shouldUpdateAccountSuccessfully () throws Exception {
            when(service.updateAccount(accountId, updateRequest))
                    .thenReturn(response);

            mockMvc.perform(put("/api/v1/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.name").value(response.name()))
                    .andExpect(jsonPath("$.type").value(response.type().toString()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()));

            verify(service, times(1)).updateAccount(accountId, updateRequest);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () throws Exception {
            doThrow(AccountNotFoundException.class)
                    .when(service)
                    .updateAccount(accountId, updateRequest);

            mockMvc.perform(put("/api/v1/accounts/{id}", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).updateAccount(accountId, updateRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badUpdateRequest = new UpdateAccountDto(null, null);

            mockMvc.perform(put("/api/v1/accounts/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}