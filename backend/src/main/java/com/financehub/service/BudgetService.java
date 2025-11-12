package com.financehub.service;

import com.financehub.dto.BudgetRequest;
import com.financehub.dto.BudgetResponse;
import com.financehub.entity.Budget;
import com.financehub.entity.Category;
import com.financehub.entity.User;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.repository.BudgetRepository;
import com.financehub.repository.CategoryRepository;
import com.financehub.repository.TransactionRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Budget management operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    
    /**
     * Get all budgets for a user
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets(Long userId) {
        log.debug("Fetching all budgets for user: {}", userId);
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        return budgets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active budgets for a user
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getActiveBudgets(Long userId) {
        log.debug("Fetching active budgets for user: {}", userId);
        List<Budget> budgets = budgetRepository.findByUserIdAndIsActive(userId, true);
        return budgets.stream()
                .map(this::updateSpentAmount)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get budget by ID
     */
    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long budgetId, Long userId) {
        log.debug("Fetching budget: {} for user: {}", budgetId, userId);
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        
        budget = updateSpentAmount(budget);
        return mapToResponse(budget);
    }
    
    /**
     * Create a new budget
     */
    @Transactional
    public BudgetResponse createBudget(BudgetRequest request, Long userId) {
        log.debug("Creating budget for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }
        
        Budget budget = Budget.builder()
                .name(request.getName())
                .amount(request.getAmount())
                .spent(BigDecimal.ZERO)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .period(request.getPeriod())
                .user(user)
                .category(category)
                .alertThreshold(request.getAlertThreshold() != null ? 
                        request.getAlertThreshold() : BigDecimal.valueOf(80))
                .isActive(true)
                .description(request.getDescription())
                .build();
        
        budget = budgetRepository.save(budget);
        budget = updateSpentAmount(budget);
        
        log.info("Budget created successfully: {}", budget.getId());
        return mapToResponse(budget);
    }
    
    /**
     * Update an existing budget
     */
    @Transactional
    public BudgetResponse updateBudget(Long budgetId, BudgetRequest request, Long userId) {
        log.debug("Updating budget: {} for user: {}", budgetId, userId);
        
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }
        
        budget.setName(request.getName());
        budget.setAmount(request.getAmount());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());
        budget.setPeriod(request.getPeriod());
        budget.setCategory(category);
        budget.setAlertThreshold(request.getAlertThreshold());
        budget.setDescription(request.getDescription());
        
        budget = budgetRepository.save(budget);
        budget = updateSpentAmount(budget);
        
        log.info("Budget updated successfully: {}", budgetId);
        return mapToResponse(budget);
    }
    
    /**
     * Delete a budget
     */
    @Transactional
    public void deleteBudget(Long budgetId, Long userId) {
        log.debug("Deleting budget: {} for user: {}", budgetId, userId);
        
        if (!budgetRepository.existsByIdAndUserId(budgetId, userId)) {
            throw new ResourceNotFoundException("Budget not found");
        }
        
        budgetRepository.deleteById(budgetId);
        log.info("Budget deleted successfully: {}", budgetId);
    }
    
    /**
     * Get budgets exceeding threshold
     */
    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgetsExceedingThreshold(Long userId) {
        log.debug("Fetching budgets exceeding threshold for user: {}", userId);
        List<Budget> budgets = budgetRepository.findBudgetsExceedingThreshold(userId);
        return budgets.stream()
                .map(this::updateSpentAmount)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update spent amount based on transactions
     */
    private Budget updateSpentAmount(Budget budget) {
        if (budget.getCategory() != null) {
            BigDecimal spent = transactionRepository.sumExpensesByCategoryAndDateRange(
                    budget.getUser().getId(),
                    budget.getCategory().getId(),
                    budget.getStartDate(),
                    budget.getEndDate()
            );
            budget.setSpent(spent != null ? spent : BigDecimal.ZERO);
        } else {
            BigDecimal spent = transactionRepository.sumExpensesByDateRange(
                    budget.getUser().getId(),
                    budget.getStartDate(),
                    budget.getEndDate()
            );
            budget.setSpent(spent != null ? spent : BigDecimal.ZERO);
        }
        return budget;
    }
    
    /**
     * Map Budget entity to BudgetResponse DTO
     */
    private BudgetResponse mapToResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .name(budget.getName())
                .amount(budget.getAmount())
                .spent(budget.getSpent())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .period(budget.getPeriod())
                .categoryId(budget.getCategory() != null ? budget.getCategory().getId() : null)
                .categoryName(budget.getCategory() != null ? budget.getCategory().getName() : null)
                .alertThreshold(budget.getAlertThreshold())
                .isActive(budget.getIsActive())
                .description(budget.getDescription())
                .spentPercentage(budget.getSpentPercentage())
                .isAlertTriggered(budget.isAlertTriggered())
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}
