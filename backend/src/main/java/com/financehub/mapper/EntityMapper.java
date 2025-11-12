package com.financehub.mapper;

import com.financehub.dto.*;
import com.financehub.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mapper utility for converting between entities and DTOs.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Component
public class EntityMapper {
    
    // Account Mappers
    public AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .color(account.getColor())
                .icon(account.getIcon())
                .isActive(account.getIsActive())
                .description(account.getDescription())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
    
    public Account toAccount(AccountRequest request) {
        return Account.builder()
                .name(request.getName())
                .type(request.getType())
                .balance(request.getBalance())
                .currency(request.getCurrency())
                .color(request.getColor())
                .icon(request.getIcon())
                .description(request.getDescription())
                .isActive(true)
                .build();
    }
    
    public void updateAccountFromRequest(Account account, AccountRequest request) {
        account.setName(request.getName());
        account.setType(request.getType());
        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency());
        account.setColor(request.getColor());
        account.setIcon(request.getIcon());
        account.setDescription(request.getDescription());
    }
    
    // Transaction Mappers
    public TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .transactionDate(transaction.getTransactionDate())
                .description(transaction.getDescription())
                .notes(transaction.getNotes())
                .accountId(transaction.getAccount().getId())
                .accountName(transaction.getAccount().getName())
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
                .payee(transaction.getPayee())
                .isRecurring(transaction.getIsRecurring())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
    
    public Transaction toTransaction(TransactionRequest request) {
        return Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .notes(request.getNotes())
                .payee(request.getPayee())
                .isRecurring(false)
                .build();
    }
    
    public void updateTransactionFromRequest(Transaction transaction, TransactionRequest request) {
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());
        transaction.setNotes(request.getNotes());
        transaction.setPayee(request.getPayee());
    }
    
    // Category Mappers
    public CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .color(category.getColor())
                .icon(category.getIcon())
                .description(category.getDescription())
                .isSystem(category.getIsSystem())
                .createdAt(category.getCreatedAt())
                .build();
    }
    
    public Category toCategory(CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .type(request.getType())
                .color(request.getColor())
                .icon(request.getIcon())
                .description(request.getDescription())
                .isSystem(false)
                .build();
    }
    
    public void updateCategoryFromRequest(Category category, CategoryRequest request) {
        category.setName(request.getName());
        category.setType(request.getType());
        category.setColor(request.getColor());
        category.setIcon(request.getIcon());
        category.setDescription(request.getDescription());
    }
    
    // Budget Mappers
    public BudgetResponse toBudgetResponse(Budget budget) {
        BigDecimal remaining = budget.getAmount().subtract(budget.getSpent());
        
        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .amount(budget.getAmount())
                .spent(budget.getSpent())
                .remaining(remaining)
                .spentPercentage(budget.getSpentPercentage())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .period(budget.getPeriod())
                .categoryId(budget.getCategory() != null ? budget.getCategory().getId() : null)
                .categoryName(budget.getCategory() != null ? budget.getCategory().getName() : null)
                .alertThreshold(budget.getAlertThreshold())
                .isAlertTriggered(budget.isAlertTriggered())
                .isActive(budget.getIsActive())
                .description(budget.getDescription())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
    
    public Budget toBudget(BudgetRequest request) {
        return Budget.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .spent(BigDecimal.ZERO)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .period(request.getPeriod())
                .alertThreshold(request.getAlertThreshold())
                .description(request.getDescription())
                .isActive(true)
                .build();
    }
    
    public void updateBudgetFromRequest(Budget budget, BudgetRequest request) {
        budget.setName(request.getName());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());
        budget.setAlertThreshold(request.getAlertThreshold());
        budget.setDescription(request.getDescription());
    }
}
