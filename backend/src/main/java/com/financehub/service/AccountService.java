package com.financehub.service;

import com.financehub.dto.AccountRequest;
import com.financehub.dto.AccountResponse;
import com.financehub.entity.Account;
import com.financehub.entity.User;
import com.financehub.exception.BadRequestException;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.mapper.EntityMapper;
import com.financehub.repository.AccountRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing user accounts.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;
    
    /**
     * Get all accounts for the current user.
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        Long userId = getCurrentUserId();
        log.debug("Fetching all accounts for user: {}", userId);
        
        List<Account> accounts = accountRepository.findByUserId(userId);
        return accounts.stream()
                .map(mapper::toAccountResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active accounts for the current user.
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getActiveAccounts() {
        Long userId = getCurrentUserId();
        log.debug("Fetching active accounts for user: {}", userId);
        
        List<Account> accounts = accountRepository.findByUserIdAndIsActive(userId, true);
        return accounts.stream()
                .map(mapper::toAccountResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get account by ID.
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        Long userId = getCurrentUserId();
        log.debug("Fetching account {} for user: {}", id, userId);
        
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        
        return mapper.toAccountResponse(account);
    }
    
    /**
     * Create a new account.
     */
    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        Long userId = getCurrentUserId();
        log.info("Creating new account for user: {}", userId);
        
        validateAccountRequest(request);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Account account = mapper.toAccount(request);
        account.setUser(user);
        
        account = accountRepository.save(account);
        log.info("Account created successfully: {}", account.getId());
        
        return mapper.toAccountResponse(account);
    }
    
    /**
     * Update an existing account.
     */
    @Transactional
    public AccountResponse updateAccount(Long id, AccountRequest request) {
        Long userId = getCurrentUserId();
        log.info("Updating account {} for user: {}", id, userId);
        
        validateAccountRequest(request);
        
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        
        mapper.updateAccountFromRequest(account, request);
        account = accountRepository.save(account);
        
        log.info("Account updated successfully: {}", account.getId());
        return mapper.toAccountResponse(account);
    }
    
    /**
     * Delete an account (soft delete).
     */
    @Transactional
    public void deleteAccount(Long id) {
        Long userId = getCurrentUserId();
        log.info("Deleting account {} for user: {}", id, userId);
        
        Account account = accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        
        account.setIsActive(false);
        accountRepository.save(account);
        
        log.info("Account deleted successfully: {}", id);
    }
    
    /**
     * Get total balance across all active accounts.
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance() {
        Long userId = getCurrentUserId();
        log.debug("Calculating total balance for user: {}", userId);
        
        BigDecimal total = accountRepository.calculateTotalBalance(userId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get total balance by account type.
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByType(Account.AccountType type) {
        Long userId = getCurrentUserId();
        log.debug("Calculating total balance for type {} and user: {}", type, userId);
        
        BigDecimal total = accountRepository.calculateTotalBalanceByType(userId, type);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private void validateAccountRequest(AccountRequest request) {
        if (request.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Account balance cannot be negative");
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        
        return user.getId();
    }
}
