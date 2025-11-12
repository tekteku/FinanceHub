package com.financehub.repository;

import com.financehub.entity.Category;
import com.financehub.entity.Category.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Category entity operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByUserId(Long userId);
    
    List<Category> findByUserIdAndType(Long userId, CategoryType type);
    
    List<Category> findByIsSystemTrue();
    
    Optional<Category> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT c FROM Category c WHERE c.isSystem = true OR c.user.id = :userId")
    List<Category> findAllAvailableForUser(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Category c WHERE (c.isSystem = true OR c.user.id = :userId) " +
           "AND c.type = :type")
    List<Category> findAllAvailableForUserByType(
            @Param("userId") Long userId,
            @Param("type") CategoryType type
    );
    
    boolean existsByIdAndUserId(Long id, Long userId);
    
    boolean existsByNameAndUserId(String name, Long userId);
}
