package com.budget_planner.budget_planner.expense.application.budget;

import com.budget_planner.budget_planner.expense.api.dto.budget.BudgetResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.CreateBudgetDto;
import com.budget_planner.budget_planner.expense.api.dto.budget.UpdateBudgetDto;
import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.domain.Budget;
import com.budget_planner.budget_planner.expense.domain.Category;
import com.budget_planner.budget_planner.expense.domain.Color;
import com.budget_planner.budget_planner.expense.domain.Period;
import com.budget_planner.budget_planner.expense.exception.BudgetNotFoundException;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.BudgetMapper;
import com.budget_planner.budget_planner.expense.persist.BudgetRepository;
import com.budget_planner.budget_planner.expense.persist.CategoryRepository;
import com.budget_planner.budget_planner.user.domain.User;
import com.budget_planner.budget_planner.user.exception.UserNotFoundException;
import com.budget_planner.budget_planner.user.persist.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Budget service unit tests")
class BudgetServiceImplTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetServiceImpl service;

    private UUID categoryId;
    private Category category;
    private Category updatedCategory;
    private CategoryResponseDto categoryResponse;
    private CategoryResponseDto updatedCategoryResponse;

    private UUID userId;
    private User user;

    private UUID budgetId;
    private Budget budget;

    private CreateBudgetDto createRequest;
    private UpdateBudgetDto updateRequest;
    private BudgetResponseDto response;
    private BudgetResponseDto responseWithUpdatedCategory;

    @BeforeEach
    void setup () {
        this.userId = UUID.randomUUID();
        this.categoryId = UUID.randomUUID();
        this.budgetId = UUID.randomUUID();

        this.user = new User("TestUser", "test@domain.com", "Test123.");
        this.category = new Category("TestCategory", Color.BLUE, user);
        this.budget = new Budget(Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), user, category);

        this.updatedCategory = new Category("TestUpdatedCategory", Color.RED, user);
        this.categoryResponse = new CategoryResponseDto(categoryId, "TestCategory", Color.BLUE.getHex(), userId);
        this.updatedCategoryResponse = new CategoryResponseDto(categoryId, "TestUpdatedCategory", Color.RED.getHex(), userId);

        this.createRequest = new CreateBudgetDto(Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), userId, categoryId);
        this.updateRequest = new UpdateBudgetDto(Period.WEEKLY, BigDecimal.TEN, null, categoryId);
        this.response = new BudgetResponseDto(budgetId, Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusMonths(1).minusDays(1), userId, categoryResponse);
        this.responseWithUpdatedCategory = new BudgetResponseDto(budgetId, Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusMonths(1).minusDays(1), userId, updatedCategoryResponse);
    }

    @Nested
    @DisplayName("Create budget tests")
    class CreateBudgetTests {

        @Test
        @DisplayName("Should create budget successfully")
        void shouldCreateBudgetSuccessfully () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));

            when(budgetMapper.createBudgetDtoToEntity(createRequest, user, category))
                    .thenReturn(budget);

            when(budgetRepository.save(any(Budget.class)))
                    .thenReturn(budget);

            when(budgetMapper.budgetToResponseDto(budget))
                    .thenReturn(response);

            var createdBudget = service.createBudget(createRequest);

            assertNotNull(createdBudget);
            assertEquals(createdBudget, response);

            verify(userRepository, times(1)).findById(userId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(budgetMapper, times(1)).createBudgetDtoToEntity(createRequest, user, category);
            verify(budgetRepository, times(1)).save(any(Budget.class));
            verify(budgetMapper, times(1)).budgetToResponseDto(budget);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.createBudget(createRequest));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(budgetMapper);
            verifyNoInteractions(budgetRepository);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(CategoryNotFoundException.class,
                    () -> service.createBudget(createRequest));

            assertNotNull(exception);
            assertEquals("Category with id " + categoryId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verifyNoInteractions(budgetMapper);
            verifyNoInteractions(budgetRepository);
        }
    }

    @Nested
    @DisplayName("Delete budget tests")
    class DeleteBudgetTests {

        @Test
        @DisplayName("Should delete budget successfully")
        void shouldDeleteBudgetSuccessfully () {
            service.deleteBudget(budgetId);
            verify(budgetRepository, times(1)).deleteById(budgetId);
        }

        @Test
        @DisplayName("Should throw BudgetNotFound exception")
        void shouldThrowBudgetNotFoundException () {
            doThrow(new EmptyResultDataAccessException(1))
                    .when(budgetRepository).deleteById(budgetId);

            var exception = assertThrows(BudgetNotFoundException.class,
                    () -> service.deleteBudget(budgetId));

            assertNotNull(exception);
            assertEquals("Budget with id " + budgetId + " was not found", exception.getMessage());

            verify(budgetRepository, times(1)).deleteById(budgetId);
        }
    }

    @Nested
    @DisplayName("Get budgets by user tests")
    class GetBudgetsByUserTests {

        @Test
        @DisplayName("Should get budgets page")
        void shouldGetBudgetsPage () {
            var pageable = PageRequest.of(0, 10);

            var firstBudget = new Budget(Period.WEEKLY, BigDecimal.ONE, LocalDate.now(), user, category);
            var secondBudget = new Budget(Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), user, category);
            var thirdBudget = new Budget(Period.YEARLY, BigDecimal.ONE, LocalDate.now(), user, category);

            var firstResponse = new BudgetResponseDto(budgetId, Period.WEEKLY, BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusDays(6), userId, null);
            var secondResponse = new BudgetResponseDto(budgetId, Period.MONTHLY, BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusMonths(1).minusDays(1), userId, null);
            var thirdResponse = new BudgetResponseDto(budgetId, Period.YEARLY, BigDecimal.ONE, LocalDate.now(), LocalDate.now().plusYears(1).minusDays(1), userId, null);

            var budgetsPage = new PageImpl<>(List.of(firstBudget, secondBudget, thirdBudget));
            var responsePage = new PageImpl<>(List.of(firstResponse, secondResponse, thirdResponse));

            when(budgetRepository.findAllByUserId(userId, pageable))
                    .thenReturn(budgetsPage);

            when(budgetMapper.budgetToResponseDto(firstBudget))
                    .thenReturn(firstResponse);

            when(budgetMapper.budgetToResponseDto(secondBudget))
                    .thenReturn(secondResponse);

            when(budgetMapper.budgetToResponseDto(thirdBudget))
                    .thenReturn(thirdResponse);

            var budgets = service.getBudgetsByUser(userId, pageable);

            assertNotNull(budgets);
            assertEquals(responsePage.getNumberOfElements(), budgetsPage.getNumberOfElements());
            assertEquals(responsePage, budgets);

            verify(budgetRepository, times(1)).findAllByUserId(userId, pageable);
            verify(budgetMapper, times(budgets.getNumberOfElements())).budgetToResponseDto(any(Budget.class));
        }

        @Test
        @DisplayName("Should get empty budgets page")
        void shouldGetEmptyBudgetsPage () {
            var pageable = PageRequest.of(0, 10);

            when(budgetRepository.findAllByUserId(userId, pageable))
                    .thenReturn(Page.empty(pageable));

            var budgets = service.getBudgetsByUser(userId, pageable);

            assertNotNull(budgets);
            assertEquals(0, budgets.getNumberOfElements());
            assertEquals(Page.empty(pageable), budgets);

            verify(budgetRepository, times(1)).findAllByUserId(userId, pageable);
            verifyNoInteractions(budgetMapper);
        }
    }

    @Nested
    @DisplayName("Get budget by id tests")
    class GetBudgetByIdTests {

        @Test
        @DisplayName("Should get budget successfully")
        void shouldGetBudgetSuccessfully () {
            when(budgetRepository.findById(budgetId))
                    .thenReturn(Optional.of(budget));

            when(budgetMapper.budgetToResponseDto(budget))
                    .thenReturn(response);

            var gotBudget = service.getBudgetById(budgetId);

            assertNotNull(gotBudget);
            assertEquals(gotBudget, response);

            verify(budgetRepository, times(1)).findById(budgetId);
            verify(budgetMapper, times(1)).budgetToResponseDto(budget);
        }

        @Test
        @DisplayName("Should throw BudgetNotFound exception")
        void shouldThrowBudgetNotFoundException () {
            when(budgetRepository.findById(budgetId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(BudgetNotFoundException.class,
                    () -> service.getBudgetById(budgetId));

            assertNotNull(exception);
            assertEquals("Budget with id " + budgetId + " was not found", exception.getMessage());

            verify(budgetRepository, times(1)).findById(budgetId);
            verifyNoInteractions(budgetMapper);
        }
    }

    @Nested
    @DisplayName("Update budget tests")
    class UpdateBudgetTests {

        @Test
        @DisplayName("Should update budget successfully")
        void shouldUpdateBudgetSuccessfully () {
            when(budgetRepository.findById(budgetId))
                    .thenReturn(Optional.of(budget));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(updatedCategory));

            when(budgetMapper.budgetToResponseDto(budget))
                    .thenReturn(responseWithUpdatedCategory);

            var updatedBudget = service.updateBudget(budgetId, updateRequest);

            assertNotNull(updatedBudget);
            assertEquals(updatedBudget, responseWithUpdatedCategory);
            assertEquals(updatedBudget.category(), updatedCategoryResponse);

            verify(budgetRepository, times(1)).findById(budgetId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(budgetMapper, times(1)).merge(budget, updateRequest, updatedCategory);
            verify(budgetMapper, times(1)).budgetToResponseDto(budget);
        }

        @Test
        @DisplayName("Should update budget keeping former category")
        void shouldUpdateBudgetKeepingFormerCategory () {
            when(budgetRepository.findById(budgetId))
                    .thenReturn(Optional.of(budget));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            when(budgetMapper.budgetToResponseDto(budget))
                    .thenReturn(response);

            var updatedBudget = service.updateBudget(budgetId, updateRequest);

            assertNotNull(updatedBudget);
            assertEquals(updatedBudget, response);
            assertEquals(updatedBudget.category(), categoryResponse);

            verify(budgetRepository, times(1)).findById(budgetId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(budgetMapper, times(1)).merge(budget, updateRequest, null);
            verify(budgetMapper, times(1)).budgetToResponseDto(budget);
        }

        @Test
        @DisplayName("Should throw BudgetNotFound exception")
        void shouldThrowBudgetNotFoundException () {
            when(budgetRepository.findById(budgetId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(BudgetNotFoundException.class,
                    () -> service.updateBudget(budgetId, updateRequest));

            assertNotNull(exception);
            assertEquals("Budget with id " + budgetId + " was not found", exception.getMessage());

            verify(budgetRepository, times(1)).findById(budgetId);
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(budgetMapper);
        }
    }
}