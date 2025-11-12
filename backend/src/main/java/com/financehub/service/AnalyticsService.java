package com.financehub.service;

import com.financehub.dto.AnalyticsSummary;
import com.financehub.dto.CategoryExpense;
import com.financehub.dto.CashFlow;
import com.financehub.dto.MonthlyTrend;
import com.financehub.entity.Account;
import com.financehub.entity.Transaction.TransactionType;
import com.financehub.repository.AccountRepository;
import com.financehub.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for analytics and reporting operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    
    /**
     * Get financial summary for a date range
     */
    @Transactional(readOnly = true)
    public AnalyticsSummary getFinancialSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching financial summary for user: {} from {} to {}", userId, startDate, endDate);
        
        BigDecimal totalIncome = transactionRepository.sumByUserIdTypeAndDateRange(
                userId, TransactionType.INCOME, startDate, endDate);
        
        BigDecimal totalExpenses = transactionRepository.sumByUserIdTypeAndDateRange(
                userId, TransactionType.EXPENSE, startDate, endDate);
        
        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpenses = totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
        
        BigDecimal balance = totalIncome.subtract(totalExpenses);
        
        return AnalyticsSummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .currency("USD")
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
    
    /**
     * Get expenses broken down by category
     */
    @Transactional(readOnly = true)
    public List<CategoryExpense> getExpensesByCategory(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching expenses by category for user: {} from {} to {}", userId, startDate, endDate);
        
        List<Object[]> results = transactionRepository.sumExpensesByCategory(userId, startDate, endDate);
        
        BigDecimal totalExpenses = results.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalExpenses.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }
        
        return results.stream()
                .map(row -> {
                    String categoryName = (String) row[0];
                    BigDecimal amount = (BigDecimal) row[1];
                    BigDecimal percentage = amount.divide(totalExpenses, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    
                    return CategoryExpense.builder()
                            .categoryName(categoryName != null ? categoryName : "Uncategorized")
                            .amount(amount)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get monthly income and expense trends
     */
    @Transactional(readOnly = true)
    public List<MonthlyTrend> getMonthlyTrends(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching monthly trends for user: {} from {} to {}", userId, startDate, endDate);
        
        List<Object[]> results = transactionRepository.getMonthlyTrends(userId, startDate, endDate);
        
        return results.stream()
                .map(row -> {
                    String month = (String) row[0];
                    BigDecimal income = (BigDecimal) row[1];
                    BigDecimal expenses = (BigDecimal) row[2];
                    BigDecimal balance = income.subtract(expenses);
                    
                    return MonthlyTrend.builder()
                            .month(month)
                            .income(income)
                            .expenses(expenses)
                            .balance(balance)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get cash flow analysis
     */
    @Transactional(readOnly = true)
    public CashFlow getCashFlow(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching cash flow for user: {} from {} to {}", userId, startDate, endDate);
        
        // Get opening balance (sum of all accounts at start date)
        List<Account> accounts = accountRepository.findByUserId(userId);
        BigDecimal currentBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get total inflows and outflows for the period
        BigDecimal totalInflows = transactionRepository.sumByUserIdTypeAndDateRange(
                userId, TransactionType.INCOME, startDate, endDate);
        
        BigDecimal totalOutflows = transactionRepository.sumByUserIdTypeAndDateRange(
                userId, TransactionType.EXPENSE, startDate, endDate);
        
        totalInflows = totalInflows != null ? totalInflows : BigDecimal.ZERO;
        totalOutflows = totalOutflows != null ? totalOutflows : BigDecimal.ZERO;
        
        // Calculate opening balance by subtracting the period's net change
        BigDecimal netChange = totalInflows.subtract(totalOutflows);
        BigDecimal openingBalance = currentBalance.subtract(netChange);
        
        return CashFlow.builder()
                .openingBalance(openingBalance)
                .totalInflows(totalInflows)
                .totalOutflows(totalOutflows)
                .closingBalance(currentBalance)
                .build();
    }
    
    /**
     * Get dashboard statistics
     */
    @Transactional(readOnly = true)
    public AnalyticsSummary getDashboardStats(Long userId) {
        log.debug("Fetching dashboard stats for user: {}", userId);
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // Last 30 days
        
        return getFinancialSummary(userId, startDate, endDate);
    }
}
