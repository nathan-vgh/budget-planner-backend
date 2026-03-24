package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.domain.Type;
import com.budget_planner.budget_planner.account.exception.AccountNotFoundException;
import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.CreateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.ExpenseResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.UpdateExpenseDto;
import com.budget_planner.budget_planner.expense.application.expense.ExpenseService;
import com.budget_planner.budget_planner.expense.domain.Color;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
import com.budget_planner.budget_planner.expense.exception.ExpenseNotFoundException;
import com.budget_planner.budget_planner.expense.exception.TagNotFoundException;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
@DisplayName("Expense controller unit tests")
class ExpenseControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService service;

    private UUID userId;
    private UUID expenseId;

    private CreateExpenseDto createRequest;
    private UpdateExpenseDto updateRequest;
    private ExpenseResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.expenseId = UUID.randomUUID();

        var accountId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();

        var account = new AccountResponseDto(accountId, "AccountTest", Type.CASH, userId);
        var category = new CategoryResponseDto(categoryId, "CategoryTest", Color.GREEN.getHex(), userId);

        this.createRequest = new CreateExpenseDto(BigDecimal.ONE, Currency.getInstance("USD"), LocalDate.now(), "ExpenseTest", userId, accountId, categoryId, null);
        this.updateRequest = new UpdateExpenseDto(BigDecimal.TEN, Currency.getInstance("EUR"), LocalDate.now().minusDays(1), "ExpenseTestUpdated", null, null, null);
        this.response = new ExpenseResponseDto(expenseId, BigDecimal.ONE, Currency.getInstance("USD"), BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), "ExpenseTest", userId, account, category, Set.of());
    }

    @Nested
    @DisplayName("Create expense tests")
    class CreateExpenseTests {

        @Test
        @DisplayName("Should create expense successfully")
        void shouldCreateExpenseSuccessfully () throws Exception {
            when(service.createExpense(createRequest))
                    .thenReturn(response);

            var expectedLocation = "/api/v1/expenses/" + response.id();

            mockMvc.perform(post("/api/v1/expenses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(expectedLocation)))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.amount").value(response.amount()))
                    .andExpect(jsonPath("$.currency").value(response.currency().getCurrencyCode()))
                    .andExpect(jsonPath("$.amountUsd").value(response.amountUsd()))
                    .andExpect(jsonPath("$.exchangeRateUsed").value(response.exchangeRateUsed()))
                    .andExpect(jsonPath("$.expenseDate").value(response.expenseDate().toString()))
                    .andExpect(jsonPath("$.description").value(response.description()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()))
                    .andExpect(jsonPath("$.account.id").value(response.account().id().toString()))
                    .andExpect(jsonPath("$.account.name").value(response.account().name()))
                    .andExpect(jsonPath("$.account.type").value(response.account().type().toString()))
                    .andExpect(jsonPath("$.account.userId").value(response.account().userId().toString()))
                    .andExpect(jsonPath("$.category.id").value(response.category().id().toString()))
                    .andExpect(jsonPath("$.category.name").value(response.category().name()))
                    .andExpect(jsonPath("$.category.color").value(response.category().color()))
                    .andExpect(jsonPath("$.category.userId").value(response.category().userId().toString()))
                    .andExpect(jsonPath("$.tags", hasSize(0)));

            verify(service, times(1)).createExpense(createRequest);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .createExpense(createRequest);

            mockMvc.perform(post("/api/v1/expenses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createExpense(createRequest);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () throws Exception {
            doThrow(AccountNotFoundException.class)
                    .when(service)
                    .createExpense(createRequest);

            mockMvc.perform(post("/api/v1/expenses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createExpense(createRequest);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () throws Exception {
            doThrow(CategoryNotFoundException.class)
                    .when(service)
                    .createExpense(createRequest);

            mockMvc.perform(post("/api/v1/expenses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createExpense(createRequest);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () throws Exception {
            doThrow(TagNotFoundException.class)
                    .when(service)
                    .createExpense(createRequest);

            mockMvc.perform(post("/api/v1/expenses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createExpense(createRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badCreateRequest = new CreateExpenseDto(null, null, null, null, null, null, null, null);

            mockMvc.perform(post("/api/v1/expenses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badCreateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Delete expense tests")
    class DeleteExpenseTests {

        @Test
        @DisplayName("Should delete expense successfully")
        void shouldDeleteExpenseSuccessfully () throws Exception {
            doNothing().when(service)
                    .deleteExpense(expenseId);

            mockMvc.perform(delete("/api/v1/expenses/{id}", expenseId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deleteExpense(expenseId);
        }

        @Test
        @DisplayName("Should throw ExpenseNotFound exception")
        void shouldThrowExpenseNotFoundException () throws Exception {
            doThrow(ExpenseNotFoundException.class)
                    .when(service)
                    .deleteExpense(expenseId);

            mockMvc.perform(delete("/api/v1/expenses/{id}", expenseId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).deleteExpense(expenseId);
        }
    }

    @Nested
    @DisplayName("Get expenses by user id tests")
    class GetExpensesByUserTests {

        @Test
        @DisplayName("Should get expenses list")
        void shouldGetExpensesList () throws Exception {
            when(service.getExpensesByUser(eq(userId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(response)));

            mockMvc.perform(get("/api/v1/expenses/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numberOfElements").value(1));

            verify(service, times(1)).getExpensesByUser(eq(userId), any(Pageable.class));
        }

        @Test
        @DisplayName("Should get empty expenses list")
        void shouldGetEmptyExpensesList () throws Exception {
            when(service.getExpensesByUser(eq(userId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get("/api/v1/expenses/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numberOfElements").value(0));

            verify(service, times(1)).getExpensesByUser(eq(userId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Get expense by id tests")
    class GetExpenseByIdTests {

        @Test
        @DisplayName("Should get expense successfully")
        void shouldGetExpenseSuccessfully () throws Exception {
            when(service.getExpenseById(expenseId))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/expenses/{id}", expenseId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.amount").value(response.amount()))
                    .andExpect(jsonPath("$.currency").value(response.currency().getCurrencyCode()))
                    .andExpect(jsonPath("$.amountUsd").value(response.amountUsd()))
                    .andExpect(jsonPath("$.exchangeRateUsed").value(response.exchangeRateUsed()))
                    .andExpect(jsonPath("$.expenseDate").value(response.expenseDate().toString()))
                    .andExpect(jsonPath("$.description").value(response.description()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()))
                    .andExpect(jsonPath("$.account.id").value(response.account().id().toString()))
                    .andExpect(jsonPath("$.account.name").value(response.account().name()))
                    .andExpect(jsonPath("$.account.type").value(response.account().type().toString()))
                    .andExpect(jsonPath("$.account.userId").value(response.account().userId().toString()))
                    .andExpect(jsonPath("$.category.id").value(response.category().id().toString()))
                    .andExpect(jsonPath("$.category.name").value(response.category().name()))
                    .andExpect(jsonPath("$.category.color").value(response.category().color()))
                    .andExpect(jsonPath("$.category.userId").value(response.category().userId().toString()))
                    .andExpect(jsonPath("$.tags", hasSize(0)));

            verify(service, times(1)).getExpenseById(expenseId);
        }

        @Test
        @DisplayName("Should throw ExpenseNotFound exception")
        void shouldThrowExpenseNotFoundException () throws Exception {
            doThrow(ExpenseNotFoundException.class)
                    .when(service)
                    .getExpenseById(expenseId);

            mockMvc.perform(get("/api/v1/expenses/{id}", expenseId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getExpenseById(expenseId);
        }
    }

    @Nested
    @DisplayName("Update expense tests")
    class UpdateExpenseTests {

        @Test
        @DisplayName("Should update expense successfully")
        void shouldUpdateExpenseSuccessfully () throws Exception {
            when(service.updateExpense(expenseId, updateRequest))
                    .thenReturn(response);

            mockMvc.perform(put("/api/v1/expenses/{id}", expenseId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.amount").value(response.amount()))
                    .andExpect(jsonPath("$.currency").value(response.currency().getCurrencyCode()))
                    .andExpect(jsonPath("$.amountUsd").value(response.amountUsd()))
                    .andExpect(jsonPath("$.exchangeRateUsed").value(response.exchangeRateUsed()))
                    .andExpect(jsonPath("$.expenseDate").value(response.expenseDate().toString()))
                    .andExpect(jsonPath("$.description").value(response.description()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()))
                    .andExpect(jsonPath("$.account.id").value(response.account().id().toString()))
                    .andExpect(jsonPath("$.account.name").value(response.account().name()))
                    .andExpect(jsonPath("$.account.type").value(response.account().type().toString()))
                    .andExpect(jsonPath("$.account.userId").value(response.account().userId().toString()))
                    .andExpect(jsonPath("$.category.id").value(response.category().id().toString()))
                    .andExpect(jsonPath("$.category.name").value(response.category().name()))
                    .andExpect(jsonPath("$.category.color").value(response.category().color()))
                    .andExpect(jsonPath("$.category.userId").value(response.category().userId().toString()))
                    .andExpect(jsonPath("$.tags", hasSize(0)));

            verify(service, times(1)).updateExpense(expenseId, updateRequest);
        }

        @Test
        @DisplayName("Should throw ExpenseNotFound exception")
        void shouldThrowExpenseNotFoundException () throws Exception {
            doThrow(ExpenseNotFoundException.class)
                    .when(service)
                    .updateExpense(expenseId, updateRequest);

            mockMvc.perform(put("/api/v1/expenses/{id}", expenseId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).updateExpense(expenseId, updateRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badUpdateRequest = new UpdateExpenseDto(null, null, null, null, null, null, null);

            mockMvc.perform(put("/api/v1/expenses/{id}", expenseId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badUpdateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }
}