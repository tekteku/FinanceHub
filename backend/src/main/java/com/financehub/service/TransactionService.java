package com.financehub.service;

import com.financehub.dto.TransactionRequest;
import com.financehub.dto.TransactionResponse;
import com.financehub.entity.Account;
import com.financehub.entity.Category;
import com.financehub.entity.Transaction;
import com.financehub.entity.User;
import com.financehub.exception.BadRequestException;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.mapper.EntityMapper;
import com.financehub.repository.AccountRepository;
import com.financehub.repository.CategoryRepository;
import com.financehub.repository.TransactionRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing transactions.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;
    
    /**
     * Get all transactions for the current user with pagination.
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(Pageable pageable) {
        Long userId = getCurrentUserId();
        log.debug("Fetching transactions for user: {}", userId);
        
        Page<Transaction> transactions = transactionRepository.findByAccountUserId(userId, pageable);
        return transactions.map(mapper::toTransactionResponse);
    }
    
    /**
     * Get transactions by type.
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByType(
            Transaction.TransactionType type, Pageable pageable) {
        Long userId = getCurrentUserId();
        log.debug("Fetching {} transactions for user: {}", type, userId);
        
        Page<Transaction> transactions = transactionRepository
                .findByAccountUserIdAndType(userId, type, pageable);
        return transactions.map(mapper::toTransactionResponse);
    }
    
    /**
     * Get transactions by account.
     */
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByAccount(Long accountId, Pageable pageable) {
        Long userId = getCurrentUserId();
        log.debug("Fetching transactions for account {} and user: {}", accountId, userId);
        
        // Verify account belongs to user
        if (!accountRepository.existsByIdAndUserId(accountId, userId)) {
            throw new ResourceNotFoundException("Account", "id", accountId);
        }
        
        Page<Transaction> transactions = transactionRepository.findByAccountId(accountId, pageable);
        return transactions.map(mapper::toTransactionResponse);
    }
    
    /**
     * Get transaction by ID.
     */
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        Long userId = getCurrentUserId();
        log.debug("Fetching transaction {} for user: {}", id, userId);
        
        Transaction transaction = transactionRepository.findByIdAndAccountUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        
        return mapper.toTransactionResponse(transaction);
    }
    
    /**
     * Get recent transactions.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentTransactions() {
        Long userId = getCurrentUserId();
        log.debug("Fetching recent transactions for user: {}", userId);
        
        List<Transaction> transactions = transactionRepository
                .findTop10ByAccountUserIdOrderByTransactionDateDesc(userId);
        
        return transactions.stream()
                .map(mapper::toTransactionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Create a new transaction.
     */
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Long userId = getCurrentUserId();
        log.info("Creating new transaction for user: {}", userId);
        
        validateTransactionRequest(request, userId);
        
        Account account = accountRepository.findByIdAndUserId(request.getAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));
        
        Transaction transaction = mapper.toTransaction(request);
        transaction.setAccount(account);
        
        // Set category if provided
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            transaction.setCategory(category);
        }
        
        // Update account balance
        updateAccountBalance(account, transaction);
        
        transaction = transactionRepository.save(transaction);
        accountRepository.save(account);
        
        log.info("Transaction created successfully: {}", transaction.getId());
        return mapper.toTransactionResponse(transaction);
    }
    
    /**
     * Update an existing transaction.
     */
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Long userId = getCurrentUserId();
        log.info("Updating transaction {} for user: {}", id, userId);
        
        validateTransactionRequest(request, userId);
        
        Transaction transaction = transactionRepository.findByIdAndAccountUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        
        Account oldAccount = transaction.getAccount();
        BigDecimal oldAmount = transaction.getAmount();
        Transaction.TransactionType oldType = transaction.getType();
        
        // Revert old transaction effect on balance
        revertAccountBalance(oldAccount, oldAmount, oldType);
        
        // Get new account if changed
        Account newAccount = oldAccount;
        if (!request.getAccountId().equals(oldAccount.getId())) {
            newAccount = accountRepository.findByIdAndUserId(request.getAccountId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));
            transaction.setAccount(newAccount);
        }
        
        // Update transaction
        mapper.updateTransactionFromRequest(transaction, request);
        
        // Set category if provided
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            transaction.setCategory(category);
        } else {
            transaction.setCategory(null);
        }
        
        // Apply new transaction effect on balance
        updateAccountBalance(newAccount, transaction);
        
        transaction = transactionRepository.save(transaction);
        accountRepository.save(oldAccount);
        if (!oldAccount.getId().equals(newAccount.getId())) {
            accountRepository.save(newAccount);
        }
        
        log.info("Transaction updated successfully: {}", transaction.getId());
        return mapper.toTransactionResponse(transaction);
    }
    
    /**
     * Delete a transaction.
     */
    @Transactional
    public void deleteTransaction(Long id) {
        Long userId = getCurrentUserId();
        log.info("Deleting transaction {} for user: {}", id, userId);
        
        Transaction transaction = transactionRepository.findByIdAndAccountUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        
        Account account = transaction.getAccount();
        
        // Revert transaction effect on balance
        revertAccountBalance(account, transaction.getAmount(), transaction.getType());
        
        transactionRepository.delete(transaction);
        accountRepository.save(account);
        
        log.info("Transaction deleted successfully: {}", id);
    }
    
    /**
     * Get transactions by date range.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionsByDateRange(
            LocalDate startDate, LocalDate endDate) {
        Long userId = getCurrentUserId();
        log.debug("Fetching transactions for user {} between {} and {}", userId, startDate, endDate);
        
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndDateRange(userId, startDate, endDate);
        
        return transactions.stream()
                .map(mapper::toTransactionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get total income for a period.
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncomeForPeriod(LocalDate startDate, LocalDate endDate) {
        Long userId = getCurrentUserId();
        log.debug("Calculating total income for user {} between {} and {}", userId, startDate, endDate);
        
        BigDecimal total = transactionRepository.sumByUserIdTypeAndDateRange(
                userId, Transaction.TransactionType.INCOME, startDate, endDate);
        
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Get total expenses for a period.
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpensesForPeriod(LocalDate startDate, LocalDate endDate) {
        Long userId = getCurrentUserId();
        log.debug("Calculating total expenses for user {} between {} and {}", userId, startDate, endDate);
        
        BigDecimal total = transactionRepository.sumByUserIdTypeAndDateRange(
                userId, Transaction.TransactionType.EXPENSE, startDate, endDate);
        
        return total != null ? total : BigDecimal.ZERO;
    }
    
    private void validateTransactionRequest(TransactionRequest request, Long userId) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Transaction amount must be greater than 0");
        }
        
        if (request.getTransactionDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Transaction date cannot be in the future");
        }
        
        if (!accountRepository.existsByIdAndUserId(request.getAccountId(), userId)) {
            throw new BadRequestException("Invalid account ID");
        }
        
        if (request.getCategoryId() != null && !categoryRepository.existsById(request.getCategoryId())) {
            throw new BadRequestException("Invalid category ID");
        }
    }
    
    private void updateAccountBalance(Account account, Transaction transaction) {
        BigDecimal newBalance = account.getBalance();
        
        switch (transaction.getType()) {
            case INCOME:
                newBalance = newBalance.add(transaction.getAmount());
                break;
            case EXPENSE:
                newBalance = newBalance.subtract(transaction.getAmount());
                break;
            case TRANSFER:
                // Transfer logic can be expanded
                break;
        }
        
        account.setBalance(newBalance);
    }
    
    private void revertAccountBalance(Account account, BigDecimal amount, Transaction.TransactionType type) {
        BigDecimal newBalance = account.getBalance();
        
        switch (type) {
            case INCOME:
                newBalance = newBalance.subtract(amount);
                break;
            case EXPENSE:
                newBalance = newBalance.add(amount);
                break;
            case TRANSFER:
                // Transfer logic can be expanded
                break;
        }
        
        account.setBalance(newBalance);
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
