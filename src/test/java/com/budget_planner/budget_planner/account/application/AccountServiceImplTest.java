package com.budget_planner.budget_planner.account.application;

import com.budget_planner.budget_planner.account.api.dto.AccountResponseDto;
import com.budget_planner.budget_planner.account.api.dto.CreateAccountDto;
import com.budget_planner.budget_planner.account.api.dto.UpdateAccountDto;
import com.budget_planner.budget_planner.account.domain.Account;
import com.budget_planner.budget_planner.account.domain.Type;
import com.budget_planner.budget_planner.account.exception.AccountNotFoundException;
import com.budget_planner.budget_planner.account.mapping.AccountMapper;
import com.budget_planner.budget_planner.account.persist.AccountRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Account service unit test")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl service;

    private UUID userId;
    private User user;

    private UUID accountId;
    private Account account;

    private CreateAccountDto createRequest;
    private UpdateAccountDto updateRequest;
    private AccountResponseDto response;

    @BeforeEach
    void setUp () {
        this.userId = UUID.randomUUID();
        this.accountId = UUID.randomUUID();

        this.user = new User("TestUser", "test@domain.com", "Test123.");
        this.account = new Account("TestAccount", Type.CASH, user);

        this.createRequest = new CreateAccountDto("TestAccount", Type.CASH, userId);
        this.updateRequest = new UpdateAccountDto("TestAccountUpdated", null);
        this.response = new AccountResponseDto(accountId, "TestAccount", Type.CASH, userId);
    }

    @Nested
    @DisplayName("Create account tests")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully")
        void shouldCreateAccountSuccessfully () {
            when(userRepository.findById(createRequest.userId()))
                    .thenReturn(Optional.of(user));

            when(accountMapper.createAccountDtoToEntity(createRequest, user))
                    .thenReturn(account);

            when(accountRepository.save(any(Account.class)))
                    .thenReturn(account);

            when(accountMapper.entityToResponseDto(account))
                    .thenReturn(response);

            var createAccount = service.createAccount(createRequest);

            assertNotNull(createAccount);
            assertEquals(createAccount, response);

            verify(userRepository, times(1)).findById(userId);
            verify(accountMapper, times(1)).createAccountDtoToEntity(createRequest, user);
            verify(accountRepository, times(1)).save(any(Account.class));
            verify(accountMapper, times(1)).entityToResponseDto(account);
        }

        @Test
        @DisplayName("Should throw UserNotFound exception")
        void shouldThrowUserNotFoundException () {
            when(userRepository.findById(createRequest.userId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(UserNotFoundException.class,
                    () -> service.createAccount(createRequest));

            assertNotNull(exception);
            assertEquals("User with id " + userId + " was not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
            verifyNoInteractions(accountMapper);
            verifyNoInteractions(accountRepository);
        }
    }

    @Nested
    @DisplayName("Delete account tests")
    class DeleteAccountTests {

        @Test
        @DisplayName("Should delete account successfully")
        void shouldDeleteAccountSuccessfully () {
            service.deleteAccount(accountId);
            verify(accountRepository, times(1)).deleteById(accountId);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () {
            doThrow(new EmptyResultDataAccessException(1))
                    .when(accountRepository).deleteById(accountId);

            var exception = assertThrows(AccountNotFoundException.class,
                    () -> service.deleteAccount(accountId));

            assertNotNull(exception);
            assertEquals("Account with id " + accountId + " was not found", exception.getMessage());

            verify(accountRepository, times(1)).deleteById(accountId);
        }
    }

    @Nested
    @DisplayName("Get accounts by user tests")
    class GetAccountsByUserTests {

        @Test
        @DisplayName("Should get accounts list")
        void shouldGetAccountsList () {
            var firstAccount = new Account("First Account", Type.CASH, user);
            var secondAccount = new Account("Second Account", Type.CREDIT, user);
            var thirdAccount = new Account("Third Account", Type.DEBIT, user);

            var firstResponse = new AccountResponseDto(accountId, "First Response", Type.CASH, userId);
            var secondResponse = new AccountResponseDto(accountId, "Second Response", Type.CREDIT, userId);
            var thirdResponse = new AccountResponseDto(accountId, "Third Response", Type.DEBIT, userId);

            var accountsList = List.of(firstAccount, secondAccount, thirdAccount);
            var responseList = List.of(firstResponse, secondResponse, thirdResponse);

            when(accountRepository.findAllByUserId(userId))
                    .thenReturn(accountsList);

            when(accountMapper.entityToResponseDto(firstAccount))
                    .thenReturn(firstResponse);

            when(accountMapper.entityToResponseDto(secondAccount))
                    .thenReturn(secondResponse);

            when(accountMapper.entityToResponseDto(thirdAccount))
                    .thenReturn(thirdResponse);

            var accounts = service.getAccountsByUser(userId);

            assertNotNull(accounts);
            assertEquals(responseList.size(), accounts.size());
            assertEquals(responseList, accounts);

            verify(accountRepository, times(1)).findAllByUserId(userId);
            verify(accountMapper, times(accounts.size())).entityToResponseDto(any(Account.class));
        }

        @Test
        @DisplayName("Should get empty accounts list")
        void shouldGetEmptyAccountsList () {
            when(accountRepository.findAllByUserId(userId))
                    .thenReturn(List.of());

            var accounts = service.getAccountsByUser(userId);

            assertNotNull(accounts);
            assertEquals(0, accounts.size());
            assertEquals(List.of(), accounts);

            verify(accountRepository, times(1)).findAllByUserId(userId);
            verifyNoInteractions(accountMapper);
        }
    }

    @Nested
    @DisplayName("Get account by id tests")
    class GetAccountByIdTests {

        @Test
        @DisplayName("Should get account successfully")
        void shouldGetAccountSuccessfully () {
            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(account));

            when(accountMapper.entityToResponseDto(account))
                    .thenReturn(response);

            var gotAccount = service.getAccountById(accountId);

            assertNotNull(gotAccount);
            assertEquals(gotAccount, response);

            verify(accountRepository, times(1)).findById(accountId);
            verify(accountMapper, times(1)).entityToResponseDto(account);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () {
            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(AccountNotFoundException.class,
                    () -> service.getAccountById(accountId));

            assertNotNull(exception);
            assertEquals("Account with id " + accountId + " was not found", exception.getMessage());

            verify(accountRepository, times(1)).findById(accountId);
            verifyNoInteractions(accountMapper);
        }
    }

    @Nested
    @DisplayName("Update account tests")
    class UpdateAccountTests {

        @Test
        @DisplayName("Should update account successfully")
        void shouldUpdateAccountSuccessfully () {
            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.of(account));

            when(accountMapper.entityToResponseDto(account))
                    .thenReturn(response);

            var updatedAccount = service.updateAccount(accountId, updateRequest);

            assertNotNull(updatedAccount);
            assertEquals(updatedAccount, response);

            verify(accountRepository, times(1)).findById(accountId);
            verify(accountMapper, times(1)).merge(account, updateRequest);
            verify(accountMapper, times(1)).entityToResponseDto(account);
        }

        @Test
        @DisplayName("Should throw AccountNotFound exception")
        void shouldThrowAccountNotFoundException () {
            when(accountRepository.findById(accountId))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(AccountNotFoundException.class,
                    () -> service.updateAccount(accountId, updateRequest));

            assertNotNull(exception);
            assertEquals("Account with id " + accountId + " was not found", exception.getMessage());

            verify(accountRepository, times(1)).findById(accountId);
            verifyNoInteractions(accountMapper);
        }
    }
}