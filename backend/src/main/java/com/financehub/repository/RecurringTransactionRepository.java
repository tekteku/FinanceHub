package com.financehub.repository;

import com.financehub.entity.RecurringTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for RecurringTransaction entity operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    
    List<RecurringTransaction> findByAccountUserId(Long userId);
    
    List<RecurringTransaction> findByAccountUserIdAndIsActive(Long userId, Boolean isActive);
    
    Optional<RecurringTransaction> findByIdAndAccountUserId(Long id, Long userId);
    
    @Query("SELECT r FROM RecurringTransaction r WHERE r.account.user.id = :userId " +
           "AND r.isActive = true AND r.nextOccurrenceDate <= :date")
    List<RecurringTransaction> findDueRecurringTransactions(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
    
    @Query("SELECT r FROM RecurringTransaction r WHERE r.isActive = true " +
           "AND r.autoCreate = true AND r.nextOccurrenceDate <= :date")
    List<RecurringTransaction> findAutoCreateDueTransactions(@Param("date") LocalDate date);
    
    boolean existsByIdAndAccountUserId(Long id, Long userId);
}
