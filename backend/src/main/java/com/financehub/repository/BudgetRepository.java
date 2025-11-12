package com.financehub.repository;

import com.financehub.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Budget entity operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByUserId(Long userId);
    
    List<Budget> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    Optional<Budget> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.startDate <= :date AND b.endDate >= :date AND b.isActive = true")
    List<Budget> findActiveByUserIdAndDate(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND b.category.id = :categoryId AND b.isActive = true")
    List<Budget> findByUserIdAndCategoryId(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId
    );
    
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId " +
           "AND (b.spent / b.amount * 100) >= b.alertThreshold AND b.isActive = true")
    List<Budget> findBudgetsExceedingThreshold(@Param("userId") Long userId);
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
