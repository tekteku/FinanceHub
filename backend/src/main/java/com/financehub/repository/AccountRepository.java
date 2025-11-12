package com.financehub.repository;

import com.financehub.entity.Account;
import com.financehub.entity.Account.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Account entity operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByUserId(Long userId);
    
    List<Account> findByUserIdAndIsActive(Long userId, Boolean isActive);
    
    List<Account> findByUserIdAndType(Long userId, AccountType type);
    
    Optional<Account> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    BigDecimal calculateTotalBalance(@Param("userId") Long userId);
    
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.type = :type AND a.isActive = true")
    BigDecimal calculateTotalBalanceByType(@Param("userId") Long userId, @Param("type") AccountType type);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    Long countActiveAccountsByUser(@Param("userId") Long userId);
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
