package com.budget_planner.budget_planner.expense.api;

import com.budget_planner.budget_planner.expense.api.dto.budget.BudgetResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.CreateBudgetDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.UpdateBudgetDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.application.budget.BudgetService;
import com.budget_planner.budget_planner.expense.domain.Color;
import com.budget_planner.budget_planner.expense.domain.Period;
import com.budget_planner.budget_planner.expense.exception.BudgetNotFoundException;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
@DisplayName("Budget controller unit tests")
class BudgetControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BudgetService service;

    private UUID userId;
    private UUID budgetId;

    private CreateBudgetDto createRequest;
    private UpdateBudgetDto updateRequest;
    private BudgetResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.budgetId = UUID.randomUUID();

        var categoryId = UUID.randomUUID();
        var category = new CategoryResponseDto(categoryId, "CategoryTest", Color.GREEN.getHex(), userId);

        this.createRequest = new CreateBudgetDto(Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), userId, categoryId);
        this.updateRequest = new UpdateBudgetDto(Period.WEEKLY, BigDecimal.TEN, null, null);
        this.response = new BudgetResponseDto(budgetId, Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusMonths(1), userId, category);
    }

    @Nested
    @DisplayName("Create budget tests")
    class CreateBudgetTests {

        @Test
        @DisplayName("Should create budget successfully")
        void shouldCreateBudgetSuccessfully () throws Exception {
            when(service.createBudget(createRequest))
                    .thenReturn(response);

            var expectedLocation = "/api/v1/budgets/" + response.id();

            mockMvc.perform(post("/api/v1/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(expectedLocation)))
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.period").value(response.period().toString()))
                    .andExpect(jsonPath("$.amount").value(response.amount().toString()))
                    .andExpect(jsonPath("$.startDate").value(response.startDate().toString()))
                    .andExpect(jsonPath("$.endDate").value(response.endDate().toString()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()))
                    .andExpect(jsonPath("$.category.id").value(response.category().id().toString()))
                    .andExpect(jsonPath("$.category.name").value(response.category().name()))
                    .andExpect(jsonPath("$.category.color").value(response.category().color()))
                    .andExpect(jsonPath("$.category.userId").value(response.category().userId().toString()));

            verify(service, times(1)).createBudget(createRequest);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () throws Exception {
            doThrow(UserNotFoundException.class)
                    .when(service)
                    .createBudget(createRequest);

            mockMvc.perform(post("/api/v1/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createBudget(createRequest);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () throws Exception {
            doThrow(CategoryNotFoundException.class)
                    .when(service)
                    .createBudget(createRequest);

            mockMvc.perform(post("/api/v1/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).createBudget(createRequest);
        }

        @Test
        @DisplayName("Should throw BadRequest exception")
        void shouldThrowBadRequestException () throws Exception {
            var badCreateRequest = new CreateBudgetDto(null, null, null, null, null);

            mockMvc.perform(post("/api/v1/budgets")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badCreateRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(service);
        }
    }

    @Nested
    @DisplayName("Delete budget tests")
    class DeleteBudgetTests {

        @Test
        @DisplayName("Should delete budget successfully")
        void shouldDeleteBudgetSuccessfully () throws Exception {
            doNothing().when(service)
                    .deleteBudget(budgetId);

            mockMvc.perform(delete("/api/v1/budgets/{id}", budgetId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).deleteBudget(budgetId);
        }

        @Test
        @DisplayName("Should throw BudgetNotFound exception")
        void shouldThrowBudgetNotFoundException () throws Exception {
            doThrow(BudgetNotFoundException.class)
                    .when(service)
                    .deleteBudget(budgetId);

            mockMvc.perform(delete("/api/v1/budgets/{id}", budgetId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).deleteBudget(budgetId);
        }
    }

    @Nested
    @DisplayName("Get budgets by user id tests")
    class GetBudgetsByUserTests {

        @Test
        @DisplayName("Should get budgets list")
        void shouldGetBudgetsList () throws Exception {
            when(service.getBudgetsByUser(eq(userId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(response)));

            mockMvc.perform(get("/api/v1/budgets/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numberOfElements").value(1));

            verify(service, times(1)).getBudgetsByUser(eq(userId), any(Pageable.class));
        }

        @Test
        @DisplayName("Should get empty budgets list")
        void shouldGetEmptyBudgetsList () throws Exception {
            when(service.getBudgetsByUser(eq(userId), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of()));

            mockMvc.perform(get("/api/v1/budgets/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numberOfElements").value(0));

            verify(service, times(1)).getBudgetsByUser(eq(userId), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Get budget by id tests")
    class GetBudgetByIdTests {

        @Test
        @DisplayName("Should get budget successfully")
        void shouldGetBudgetSuccessfully () throws Exception {
            when(service.getBudgetById(budgetId))
                    .thenReturn(response);

            mockMvc.perform(get("/api/v1/budgets/{id}", budgetId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.period").value(response.period().toString()))
                    .andExpect(jsonPath("$.amount").value(response.amount().toString()))
                    .andExpect(jsonPath("$.startDate").value(response.startDate().toString()))
                    .andExpect(jsonPath("$.endDate").value(response.endDate().toString()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()))
                    .andExpect(jsonPath("$.category.id").value(response.category().id().toString()))
                    .andExpect(jsonPath("$.category.name").value(response.category().name()))
                    .andExpect(jsonPath("$.category.color").value(response.category().color()))
                    .andExpect(jsonPath("$.category.userId").value(response.category().userId().toString()));

            verify(service, times(1)).getBudgetById(budgetId);
        }

        @Test
        @DisplayName("Should throw BudgetNotFound exception")
        void shouldThrowBudgetNotFoundException () throws Exception {
            doThrow(BudgetNotFoundException.class)
                    .when(service)
                    .getBudgetById(budgetId);

            mockMvc.perform(get("/api/v1/budgets/{id}", budgetId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).getBudgetById(budgetId);
        }
    }

    @Nested
    @DisplayName("Update budget tests")
    class UpdateBudgetTests {

        @Test
        @DisplayName("Should update budget successfully")
        void shouldUpdateBudgetSuccessfully () throws Exception {
            when(service.updateBudget(budgetId, updateRequest))
                    .thenReturn(response);

            mockMvc.perform(put("/api/v1/budgets/{id}", budgetId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(response.id().toString()))
                    .andExpect(jsonPath("$.period").value(response.period().toString()))
                    .andExpect(jsonPath("$.amount").value(response.amount().toString()))
                    .andExpect(jsonPath("$.startDate").value(response.startDate().toString()))
                    .andExpect(jsonPath("$.endDate").value(response.endDate().toString()))
                    .andExpect(jsonPath("$.userId").value(response.userId().toString()))
                    .andExpect(jsonPath("$.category.id").value(response.category().id().toString()))
                    .andExpect(jsonPath("$.category.name").value(response.category().name()))
                    .andExpect(jsonPath("$.category.color").value(response.category().color()))
                    .andExpect(jsonPath("$.category.userId").value(response.category().userId().toString()));

            verify(service, times(1)).updateBudget(budgetId, updateRequest);
        }

        @Test
        @DisplayName("Should throw BudgetNotFound exception")
        void shouldThrowBudgetNotFoundException () throws Exception {
            doThrow(BudgetNotFoundException.class)
                    .when(service)
                    .updateBudget(budgetId, updateRequest);

            mockMvc.perform(put("/api/v1/budgets/{id}", budgetId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound());

            verify(service, times(1)).updateBudget(budgetId, updateRequest);
        }
    }
}