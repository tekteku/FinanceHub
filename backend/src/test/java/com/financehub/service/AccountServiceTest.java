package com.financehub.service;

import com.financehub.dto.AccountRequest;
import com.financehub.dto.AccountResponse;
import com.financehub.entity.Account;
import com.financehub.entity.User;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.mapper.EntityMapper;
import com.financehub.repository.AccountRepository;
import com.financehub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
class AccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EntityMapper mapper;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AccountService accountService;
    
    private User testUser;
    private Account testAccount;
    private AccountRequest testRequest;
    private AccountResponse testResponse;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        testAccount = Account.builder()
                .id(1L)
                .name("Test Account")
                .type(Account.AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .currency("USD")
                .isActive(true)
                .user(testUser)
                .build();
        
        testRequest = new AccountRequest();
        testRequest.setName("Test Account");
        testRequest.setType(Account.AccountType.CHECKING);
        testRequest.setBalance(BigDecimal.valueOf(1000));
        testRequest.setCurrency("USD");
        
        testResponse = AccountResponse.builder()
                .id(1L)
                .name("Test Account")
                .type(Account.AccountType.CHECKING)
                .balance(BigDecimal.valueOf(1000))
                .currency("USD")
                .isActive(true)
                .build();
        
        // Setup Security Context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }
    
    @Test
    @DisplayName("Should get all accounts for user")
    void shouldGetAllAccounts() {
        // Given
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findByUserId(1L)).thenReturn(accounts);
        when(mapper.toAccountResponse(testAccount)).thenReturn(testResponse);
        
        // When
        List<AccountResponse> result = accountService.getAllAccounts();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Account");
        verify(accountRepository).findByUserId(1L);
        verify(mapper).toAccountResponse(testAccount);
    }
    
    @Test
    @DisplayName("Should get active accounts only")
    void shouldGetActiveAccounts() {
        // Given
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findByUserIdAndIsActive(1L, true)).thenReturn(accounts);
        when(mapper.toAccountResponse(testAccount)).thenReturn(testResponse);
        
        // When
        List<AccountResponse> result = accountService.getActiveAccounts();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(accountRepository).findByUserIdAndIsActive(1L, true);
    }
    
    @Test
    @DisplayName("Should get account by ID")
    void shouldGetAccountById() {
        // Given
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAccount));
        when(mapper.toAccountResponse(testAccount)).thenReturn(testResponse);
        
        // When
        AccountResponse result = accountService.getAccountById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Account");
        verify(accountRepository).findByIdAndUserId(1L, 1L);
    }
    
    @Test
    @DisplayName("Should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        // Given
        when(accountRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());
        
        // When / Then
        assertThatThrownBy(() -> accountService.getAccountById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Account not found");
        
        verify(accountRepository).findByIdAndUserId(999L, 1L);
    }
    
    @Test
    @DisplayName("Should create new account")
    void shouldCreateAccount() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toAccount(testRequest)).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(mapper.toAccountResponse(testAccount)).thenReturn(testResponse);
        
        // When
        AccountResponse result = accountService.createAccount(testRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Account");
        verify(accountRepository).save(any(Account.class));
    }
    
    @Test
    @DisplayName("Should update existing account")
    void shouldUpdateAccount() {
        // Given
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(mapper.toAccountResponse(testAccount)).thenReturn(testResponse);
        
        // When
        AccountResponse result = accountService.updateAccount(1L, testRequest);
        
        // Then
        assertThat(result).isNotNull();
        verify(mapper).updateAccountFromRequest(testAccount, testRequest);
        verify(accountRepository).save(testAccount);
    }
    
    @Test
    @DisplayName("Should delete account (soft delete)")
    void shouldDeleteAccount() {
        // Given
        when(accountRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        
        // When
        accountService.deleteAccount(1L);
        
        // Then
        verify(accountRepository).save(testAccount);
        assertThat(testAccount.getIsActive()).isFalse();
    }
    
    @Test
    @DisplayName("Should calculate total balance")
    void shouldCalculateTotalBalance() {
        // Given
        BigDecimal expectedBalance = BigDecimal.valueOf(5000);
        when(accountRepository.calculateTotalBalance(1L)).thenReturn(expectedBalance);
        
        // When
        BigDecimal result = accountService.getTotalBalance();
        
        // Then
        assertThat(result).isEqualByComparingTo(expectedBalance);
        verify(accountRepository).calculateTotalBalance(1L);
    }
    
    @Test
    @DisplayName("Should return zero when no balance")
    void shouldReturnZeroWhenNoBalance() {
        // Given
        when(accountRepository.calculateTotalBalance(1L)).thenReturn(null);
        
        // When
        BigDecimal result = accountService.getTotalBalance();
        
        // Then
        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
