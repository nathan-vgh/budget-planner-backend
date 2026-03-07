package com.budget_planner.budget_planner.expense.application.expense;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.domain.Account;
import com.budget_planner.budget_planner.account.domain.Type;
import com.budget_planner.budget_planner.account.exception.AccountNotFoundException;
import com.budget_planner.budget_planner.account.persist.AccountRepository;
import com.budget_planner.budget_planner.expense.api.dto.category.CategoryResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.CreateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.ExpenseResponseDto;
import com.budget_planner.budget_planner.expense.api.dto.expense.UpdateExpenseDto;
import com.budget_planner.budget_planner.expense.api.dto.tag.TagResponseDto;
import com.budget_planner.budget_planner.expense.domain.Category;
import com.budget_planner.budget_planner.expense.domain.Color;
import com.budget_planner.budget_planner.expense.domain.Expense;
import com.budget_planner.budget_planner.expense.domain.Tag;
import com.budget_planner.budget_planner.expense.exception.CategoryNotFoundException;
import com.budget_planner.budget_planner.expense.exception.ExpenseNotFoundException;
import com.budget_planner.budget_planner.expense.exception.TagNotFoundException;
import com.budget_planner.budget_planner.expense.mapping.ExpenseMapper;
import com.budget_planner.budget_planner.expense.persist.CategoryRepository;
import com.budget_planner.budget_planner.expense.persist.ExpenseRepository;
import com.budget_planner.budget_planner.expense.persist.TagRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Expense service unit test")
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl service;

    private UUID userId;
    private User user;

    private UUID accountId;
    private Account account;
    private Account updatedAccount;
    private AccountResponseDto accountResponse;
    private AccountResponseDto updatedAccountResponse;

    private UUID categoryId;
    private Category category;
    private Category updatedCategory;
    private CategoryResponseDto categoryResponse;
    private CategoryResponseDto updatedCategoryResponse;

    private Set<UUID> tagsId;
    private List<Tag> tagsList;
    private List<Tag> updatedTagsList;
    private Set<TagResponseDto> tagsResponseSet;
    private Set<TagResponseDto> updatedTagsResponseSet;

    private UUID expenseId;
    private Expense expense;

    private CreateExpenseDto createRequest;
    private UpdateExpenseDto updateRequest;
    private ExpenseResponseDto response;
    private ExpenseResponseDto responseWithoutUpdateAccount;
    private ExpenseResponseDto responseWithoutUpdateCategory;
    private ExpenseResponseDto responseWithoutUpdateTags;
    private ExpenseResponseDto responseUpdated;

    @BeforeEach
    void setup() {
        this.userId = UUID.randomUUID();
        this.accountId = UUID.randomUUID();
        this.categoryId = UUID.randomUUID();
        this.expenseId = UUID.randomUUID();

        this.user = new User("TestUser", "test@domain.com", "Test123.");
        this.account = new Account("TestAccount", Type.CASH, user);
        this.category = new Category("TestCategory", Color.BLUE, user);
        this.expense = new Expense(BigDecimal.ONE, Currency.getInstance("USD"), LocalDate.now(), "TestExpense", user, account, category, Set.of());

        this.accountResponse = new AccountResponseDto(accountId, "TestAccount", Type.CASH, userId);
        this.categoryResponse = new CategoryResponseDto(categoryId, "TestCategory", Color.BLUE.getHex(), userId);

        this.updatedAccount = new Account("TestUpdatedAccount", Type.DEBIT, user);
        this.updatedCategory = new Category("TestUpdatedCategory", Color.RED, user);

        this.updatedAccountResponse = new AccountResponseDto(accountId, "TestUpdatedAccount", Type.DEBIT, userId);
        this.updatedCategoryResponse = new CategoryResponseDto(categoryId, "TestUpdatedCategory", Color.RED.getHex(), userId);

        var firstTagId = UUID.randomUUID();
        var secondTagId = UUID.randomUUID();
        var thirdTagId = UUID.randomUUID();

        var firstTag = new Tag("TestFirstTag", user);
        var secondTag = new Tag("TestSecondTag", user);
        var thirdTag = new Tag("TestThirdTag", user);

        var firstTagResponse = new TagResponseDto(firstTagId, "TestFirstTag", userId);
        var secondTagResponse = new TagResponseDto(secondTagId, "TestSecondTag", userId);
        var thirdTagResponse = new TagResponseDto(thirdTagId, "TestThirdTag", userId);

        var updatedFirstTag = new Tag("TestUpdatedFirstTag", user);
        var updatedSecondTag = new Tag("TestUpdatedSecondTag", user);
        var updatedThirdTag = new Tag("TestUpdatedThirdTag", user);

        var updatedFirstTagResponse = new TagResponseDto(firstTagId, "TestUpdatedFirstTag", userId);
        var updatedSecondTagResponse = new TagResponseDto(secondTagId, "TestUpdatedSecondTag", userId);
        var updatedThirdTagResponse = new TagResponseDto(thirdTagId, "TestUpdatedThirdTag", userId);

        this.tagsId = Set.of(firstTagId, secondTagId, thirdTagId);
        this.tagsList = List.of(firstTag, secondTag, thirdTag);
        this.updatedTagsList = List.of(updatedFirstTag, updatedSecondTag, updatedThirdTag);
        this.tagsResponseSet = Set.of(firstTagResponse, secondTagResponse, thirdTagResponse);
        this.updatedTagsResponseSet = Set.of(updatedFirstTagResponse, updatedSecondTagResponse, updatedThirdTagResponse);

        this.createRequest = new CreateExpenseDto(BigDecimal.ONE, Currency.getInstance("USD"), LocalDate.now(), "TestExpense", userId, accountId, categoryId, tagsId);
        this.updateRequest = new UpdateExpenseDto(BigDecimal.TEN, Currency.getInstance("EUR"), LocalDate.now().plusDays(1), "TestUpdatedExpense", accountId, categoryId, tagsId);
        this.response = new ExpenseResponseDto(expenseId, BigDecimal.ONE, Currency.getInstance("USD"), BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), "TestUpdatedExpense", userId, accountResponse, categoryResponse, tagsResponseSet);
        this.responseWithoutUpdateAccount = new ExpenseResponseDto(expenseId, BigDecimal.ONE, Currency.getInstance("USD"), BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), "TestUpdatedExpense", userId, accountResponse, updatedCategoryResponse, updatedTagsResponseSet);
        this.responseWithoutUpdateCategory = new ExpenseResponseDto(expenseId, BigDecimal.ONE, Currency.getInstance("USD"), BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), "TestUpdatedExpense", userId, updatedAccountResponse, categoryResponse, updatedTagsResponseSet);
        this.responseWithoutUpdateTags = new ExpenseResponseDto(expenseId, BigDecimal.ONE, Currency.getInstance("USD"), BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), "TestUpdatedExpense", userId, updatedAccountResponse, updatedCategoryResponse, tagsResponseSet);
        this.responseUpdated = new ExpenseResponseDto(expenseId, BigDecimal.ONE, Currency.getInstance("USD"), BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), "TestUpdatedExpense", userId, updatedAccountResponse, updatedCategoryResponse, updatedTagsResponseSet);
    }

    @Nested
    @DisplayName("Create expense tests")
    class CreateExpenseTests {

        @Test
        @DisplayName("Should create expense successfully")
        void shouldCreateExpenseSuccessfully () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(account));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));

            when(tagRepository.findAllById(tagsId))
                    .thenReturn(tagsList);

            when(expenseMapper.createExpenseDtoToEntity(createRequest, user, account, category, new HashSet<>(tagsList)))
                    .thenReturn(expense);

            when(expenseRepository.save(any(Expense.class)))
                    .thenReturn(expense);

            when(expenseMapper.expenseToResponseDto(expense))
                    .thenReturn(response);

            var createdExpense = service.createExpense(createRequest);

            assertNotNull(createdExpense);
            assertEquals(createdExpense, response);

            verify(userRepository, times(1)).findById(userId);
            verify(accountRepository, times(1)).findById(accountId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(tagRepository, times(1)).findAllById(tagsId);
            verify(expenseMapper, times(1)).createExpenseDtoToEntity(createRequest, user, account, category, new HashSet<>(tagsList));
            verify(expenseRepository, times(1)).save(any(Expense.class));
            verify(expenseMapper, times(1)).expenseToResponseDto(expense);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.createExpense(createRequest));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verifyNoInteractions(accountRepository);
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(expenseMapper);
            verifyNoInteractions(expenseRepository);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(AccountNotFoundException.class,
                    () -> service.createExpense(createRequest));

            assertNotNull(exception);
            assertEquals("Account with id " + accountId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verify(accountRepository, times(1)).findById(accountId);
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(expenseMapper);
            verifyNoInteractions(expenseRepository);
        }

        @Test
        @DisplayName("Should throw CategoryNotFound exception")
        void shouldThrowCategoryNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(account));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(CategoryNotFoundException.class,
                    () -> service.createExpense(createRequest));

            assertNotNull(exception);
            assertEquals("Category with id " + categoryId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verify(accountRepository, times(1)).findById(accountId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(expenseMapper);
            verifyNoInteractions(expenseRepository);
        }

        @Test
        @DisplayName("Should throw TagNotFound exception")
        void shouldThrowTagNotFoundException () {
            when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(account));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));

            when(tagRepository.findAllById(tagsId))
                    .thenReturn(List.of());

            var exception = assertThrows(TagNotFoundException.class,
                    () -> service.createExpense(createRequest));

            assertNotNull(exception);
            assertEquals("Some UUID on the tag list don't exist", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verify(accountRepository, times(1)).findById(accountId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(tagRepository, times(1)).findAllById(tagsId);
            verifyNoInteractions(expenseMapper);
            verifyNoInteractions(expenseRepository);
        }
    }

    @Nested
    @DisplayName("Delete expense tests")
    class DeleteExpenseTests {

        @Test
        @DisplayName("Should delete expense successfully")
        void shouldDeleteExpenseSuccessfully () {
            service.deleteExpense(expenseId);
            verify(expenseRepository, times(1)).deleteById(expenseId);
        }

        @Test
        @DisplayName("Should throw ExpenseNotFound exception")
        void shouldThrowExpenseNotFoundException () {
            doThrow(new EmptyResultDataAccessException(1))
                    .when(expenseRepository).deleteById(expenseId);

            var exception = assertThrows(ExpenseNotFoundException.class,
                    () -> service.deleteExpense(expenseId));

            assertNotNull(expense);
            assertEquals("Expense with id " + expenseId + " was not found", exception.getMessage());

            verify(expenseRepository, times(1)).deleteById(expenseId);
        }
    }

    @Nested
    @DisplayName("Get expenses by the user tests")
    class GetExpensesByUserTests {

        @Test
        @DisplayName("Should get expenses page")
        void shouldGetExpensesPage () {
            var pageable = PageRequest.of(0, 10);

            var firstExpense = new Expense(BigDecimal.ONE, Currency.getInstance("USD"), LocalDate.now(), "FirstExpense", user, account, category, null);
            var secondExpense = new Expense(BigDecimal.TWO, Currency.getInstance("EUR"), LocalDate.now(), "SecondExpense", user, account, category, null);
            var thirdExpense = new Expense(BigDecimal.TEN, Currency.getInstance("CRC"), LocalDate.now(), "ThirdExpense", user, account, category, null);

            var firstResponse = new ExpenseResponseDto(expenseId, BigDecimal.ONE, Currency.getInstance("USD"), BigDecimal.ONE, BigDecimal.ONE, LocalDate.now(), "FirstResponse", userId, null, null, null);
            var secondResponse = new ExpenseResponseDto(expenseId, BigDecimal.TWO, Currency.getInstance("EUR"), BigDecimal.TWO, BigDecimal.TWO, LocalDate.now(), "SecondResponse", userId, null, null, null);
            var thirdResponse = new ExpenseResponseDto(expenseId, BigDecimal.TEN, Currency.getInstance("CRC"), BigDecimal.TEN, BigDecimal.TEN, LocalDate.now(), "ThirdResponse", userId, null, null, null);

            var expensePage = new PageImpl<>(List.of(firstExpense, secondExpense, thirdExpense));
            var responsePage = new PageImpl<>(List.of(firstResponse, secondResponse, thirdResponse));

            when(expenseRepository.findAllByUserId(userId, pageable))
                    .thenReturn(expensePage);

            when(expenseMapper.expenseToResponseDto(firstExpense))
                    .thenReturn(firstResponse);

            when(expenseMapper.expenseToResponseDto(secondExpense))
                    .thenReturn(secondResponse);

            when(expenseMapper.expenseToResponseDto(thirdExpense))
                    .thenReturn(thirdResponse);

            var expenses = service.getExpensesByUser(userId, pageable);

            assertNotNull(expenses);
            assertEquals(responsePage.getNumberOfElements(), expenses.getNumberOfElements());
            assertEquals(responsePage, expenses);

            verify(expenseRepository, times(1)).findAllByUserId(userId, pageable);
            verify(expenseMapper, times(expenses.getNumberOfElements())).expenseToResponseDto(any(Expense.class));
        }

        @Test
        @DisplayName("Should get empty expenses page")
        void shouldGetEmptyExpensesPage () {
            var pageable = PageRequest.of(0, 10);

            when(expenseRepository.findAllByUserId(userId, pageable))
                    .thenReturn(Page.empty(pageable));

            var expenses = service.getExpensesByUser(userId, pageable);

            assertNotNull(expenses);
            assertEquals(0, expenses.getNumberOfElements());
            assertEquals(Page.empty(pageable), expenses);

            verify(expenseRepository, times(1)).findAllByUserId(userId, pageable);
            verifyNoInteractions(expenseMapper);
        }
    }

    @Nested
    @DisplayName("Get expense by id tests")
    class GetExpenseByIdTests {

        @Test
        @DisplayName("Should get expense successfully")
        void shouldGetExpenseSuccessfully () {
            when(expenseRepository.findById(expenseId))
                    .thenReturn(Optional.of(expense));

            when(expenseMapper.expenseToResponseDto(expense))
                    .thenReturn(response);

            var gotExpense = service.getExpenseById(expenseId);

            assertNotNull(gotExpense);
            assertEquals(gotExpense, response);

            verify(expenseRepository, times(1)).findById(expenseId);
            verify(expenseMapper, times(1)).expenseToResponseDto(expense);
        }

        @Test
        @DisplayName("Should throw ExpenseNotFound exception")
        void shouldThrowExpenseNotFoundException () {
            when(expenseRepository.findById(expenseId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(ExpenseNotFoundException.class,
                    () -> service.getExpenseById(expenseId));

            assertNotNull(exception);
            assertEquals("Expense with id " + expenseId + " was not found", exception.getMessage());

            verify(expenseRepository, times(1)).findById(expenseId);
            verifyNoInteractions(expenseMapper);
        }
    }

    @Nested
    @DisplayName("Update expense tests")
    class UpdateExpenseTests {

        @Test
        @DisplayName("Should update expense successfully")
        void shouldUpdateExpenseSuccessfully () {
            when(expenseRepository.findById(expenseId))
                    .thenReturn(Optional.of(expense));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(updatedAccount));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(updatedCategory));

            when(tagRepository.findAllById(tagsId))
                    .thenReturn(updatedTagsList);

            when(expenseMapper.expenseToResponseDto(expense))
                    .thenReturn(responseUpdated);

            var updatedExpense = service.updateExpense(expenseId, updateRequest);

            assertNotNull(updatedExpense);
            assertEquals(updatedExpense, responseUpdated);
            assertEquals(updatedExpense.account(), updatedAccountResponse);
            assertEquals(updatedExpense.category(), updatedCategoryResponse);
            assertEquals(updatedExpense.tags(), updatedTagsResponseSet);

            verify(expenseRepository, times(1)).findById(expenseId);
            verify(accountRepository, times(1)).findById(accountId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(tagRepository, times(1)).findAllById(tagsId);
            verify(expenseMapper, times(1)).merge(expense, updateRequest, updatedAccount, updatedCategory, new HashSet<>(updatedTagsList));
            verify(expenseMapper, times(1)).expenseToResponseDto(expense);
        }

        @Test
        @DisplayName("Should update expense keeping former account")
        void shouldUpdateExpenseKeepingFormerAccount () {
            when(expenseRepository.findById(expenseId))
                    .thenReturn(Optional.of(expense));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.empty());

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(updatedCategory));

            when(tagRepository.findAllById(tagsId))
                    .thenReturn(updatedTagsList);

            when(expenseMapper.expenseToResponseDto(expense))
                    .thenReturn(responseWithoutUpdateAccount);

            var updatedExpense = service.updateExpense(expenseId, updateRequest);

            assertNotNull(updatedExpense);
            assertEquals(updatedExpense, responseWithoutUpdateAccount);
            assertEquals(updatedExpense.account(), accountResponse);
            assertEquals(updatedExpense.category(), updatedCategoryResponse);
            assertEquals(updatedExpense.tags(), updatedTagsResponseSet);

            verify(expenseRepository, times(1)).findById(expenseId);
            verify(accountRepository, times(1)).findById(accountId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(tagRepository, times(1)).findAllById(tagsId);
            verify(expenseMapper, times(1)).merge(expense, updateRequest, null, updatedCategory, new HashSet<>(updatedTagsList));
            verify(expenseMapper, times(1)).expenseToResponseDto(expense);
        }

        @Test
        @DisplayName("Should update expense keeping former category")
        void shouldUpdateExpenseKeepingFormerCategory () {
            when(expenseRepository.findById(expenseId))
                    .thenReturn(Optional.of(expense));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(updatedAccount));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            when(tagRepository.findAllById(tagsId))
                    .thenReturn(updatedTagsList);

            when(expenseMapper.expenseToResponseDto(expense))
                    .thenReturn(responseWithoutUpdateCategory);

            var updatedExpense = service.updateExpense(expenseId, updateRequest);

            assertNotNull(updatedExpense);
            assertEquals(updatedExpense, responseWithoutUpdateCategory);
            assertEquals(updatedExpense.account(), updatedAccountResponse);
            assertEquals(updatedExpense.category(), categoryResponse);
            assertEquals(updatedExpense.tags(), updatedTagsResponseSet);

            verify(expenseRepository, times(1)).findById(expenseId);
            verify(accountRepository, times(1)).findById(accountId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(tagRepository, times(1)).findAllById(tagsId);
            verify(expenseMapper, times(1)).merge(expense, updateRequest, updatedAccount, null, new HashSet<>(updatedTagsList));
            verify(expenseMapper, times(1)).expenseToResponseDto(expense);
        }

        @Test
        @DisplayName("Should update expense keeping former tags")
        void shouldUpdateExpenseKeepingFormerTags () {
            when(expenseRepository.findById(expenseId))
                    .thenReturn(Optional.of(expense));

            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(updatedAccount));

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(updatedCategory));

            when(tagRepository.findAllById(tagsId))
                    .thenReturn(List.of());

            when(expenseMapper.expenseToResponseDto(expense))
                    .thenReturn(responseWithoutUpdateTags);

            var updatedExpense = service.updateExpense(expenseId, updateRequest);

            assertNotNull(updatedExpense);
            assertEquals(updatedExpense, responseWithoutUpdateTags);
            assertEquals(updatedExpense.account(), updatedAccountResponse);
            assertEquals(updatedExpense.category(), updatedCategoryResponse);
            assertEquals(updatedExpense.tags(), tagsResponseSet);

            verify(expenseRepository, times(1)).findById(expenseId);
            verify(accountRepository, times(1)).findById(accountId);
            verify(categoryRepository, times(1)).findById(categoryId);
            verify(tagRepository, times(1)).findAllById(tagsId);
            verify(expenseMapper, times(1)).merge(expense, updateRequest, updatedAccount, updatedCategory, Set.of());
            verify(expenseMapper, times(1)).expenseToResponseDto(expense);
        }

        @Test
        @DisplayName("Should throw ExpenseNotFount exception")
        void shouldThrowExpenseNotFoundException () {
            when(expenseRepository.findById(expenseId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(ExpenseNotFoundException.class,
                    () -> service.updateExpense(expenseId, updateRequest));

            assertNotNull(exception);
            assertEquals("Expense with id " + expenseId + " was not found", exception.getMessage());

            verify(expenseRepository, times(1)).findById(expenseId);
            verifyNoInteractions(accountRepository);
            verifyNoInteractions(categoryRepository);
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(expenseMapper);
        }
    }
}