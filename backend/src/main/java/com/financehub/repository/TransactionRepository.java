package com.financehub.repository;

import com.financehub.entity.Transaction;
import com.financehub.entity.Transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Transaction entity operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Page<Transaction> findByAccountUserId(Long userId, Pageable pageable);
    
    Page<Transaction> findByAccountUserIdAndType(Long userId, TransactionType type, Pageable pageable);
    
    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);
    
    Optional<Transaction> findByIdAndAccountUserId(Long id, Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.account.user.id = :userId " +
           "AND t.type = :type AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t FROM Transaction t WHERE t.category.id = :categoryId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByCategoryIdAndDateRange(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.category.id = :categoryId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByCategoryIdAndDateRange(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    List<Transaction> findTop10ByAccountUserIdOrderByTransactionDateDesc(Long userId);
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND t.category.id = :categoryId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpensesByCategoryAndDateRange(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
           "WHERE t.account.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpensesByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT t.category.name as categoryName, COALESCE(SUM(t.amount), 0) as amount " +
           "FROM Transaction t " +
           "WHERE t.account.user.id = :userId AND t.type = 'EXPENSE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.category.name " +
           "ORDER BY amount DESC")
    List<Object[]> sumExpensesByCategory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT FUNCTION('TO_CHAR', t.transactionDate, 'YYYY-MM') as month, " +
           "COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END), 0) as income, " +
           "COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END), 0) as expenses " +
           "FROM Transaction t " +
           "WHERE t.account.user.id = :userId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('TO_CHAR', t.transactionDate, 'YYYY-MM') " +
           "ORDER BY month")
    List<Object[]> getMonthlyTrends(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
