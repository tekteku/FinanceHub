package com.financehub.service;

import com.financehub.dto.CategoryRequest;
import com.financehub.dto.CategoryResponse;
import com.financehub.entity.Category;
import com.financehub.entity.User;
import com.financehub.exception.BadRequestException;
import com.financehub.exception.DuplicateResourceException;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.mapper.EntityMapper;
import com.financehub.repository.CategoryRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing categories.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;
    
    /**
     * Get all categories available for the current user (system + user's own).
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        Long userId = getCurrentUserId();
        log.debug("Fetching all categories for user: {}", userId);
        
        List<Category> categories = categoryRepository.findAllAvailableForUser(userId);
        return categories.stream()
                .map(mapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get categories by type.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(Category.CategoryType type) {
        Long userId = getCurrentUserId();
        log.debug("Fetching {} categories for user: {}", type, userId);
        
        List<Category> categories = categoryRepository.findAllAvailableForUserByType(userId, type);
        return categories.stream()
                .map(mapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get category by ID.
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Long userId = getCurrentUserId();
        log.debug("Fetching category {} for user: {}", id, userId);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Verify access (system category or user's own category)
        if (!category.getIsSystem() && !category.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        
        return mapper.toCategoryResponse(category);
    }
    
    /**
     * Create a new category.
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Long userId = getCurrentUserId();
        log.info("Creating new category for user: {}", userId);
        
        // Check for duplicate name
        if (categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new DuplicateResourceException("Category", "name", request.getName());
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Category category = mapper.toCategory(request);
        category.setUser(user);
        category.setIsSystem(false);
        
        category = categoryRepository.save(category);
        log.info("Category created successfully: {}", category.getId());
        
        return mapper.toCategoryResponse(category);
    }
    
    /**
     * Update an existing category.
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Long userId = getCurrentUserId();
        log.info("Updating category {} for user: {}", id, userId);
        
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Cannot update system categories
        if (category.getIsSystem()) {
            throw new BadRequestException("Cannot update system categories");
        }
        
        mapper.updateCategoryFromRequest(category, request);
        category = categoryRepository.save(category);
        
        log.info("Category updated successfully: {}", category.getId());
        return mapper.toCategoryResponse(category);
    }
    
    /**
     * Delete a category.
     */
    @Transactional
    public void deleteCategory(Long id) {
        Long userId = getCurrentUserId();
        log.info("Deleting category {} for user: {}", id, userId);
        
        Category category = categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // Cannot delete system categories
        if (category.getIsSystem()) {
            throw new BadRequestException("Cannot delete system categories");
        }
        
        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", id);
    }
    
    /**
     * Initialize system categories.
     */
    @Transactional
    public void initializeSystemCategories() {
        log.info("Initializing system categories");
        
        if (categoryRepository.findByIsSystemTrue().isEmpty()) {
            // Income categories
            createSystemCategory("Salary", Category.CategoryType.INCOME, "#10B981", "💰");
            createSystemCategory("Freelance", Category.CategoryType.INCOME, "#3B82F6", "💼");
            createSystemCategory("Investment", Category.CategoryType.INCOME, "#8B5CF6", "📈");
            createSystemCategory("Other Income", Category.CategoryType.INCOME, "#6B7280", "💵");
            
            // Expense categories
            createSystemCategory("Food & Dining", Category.CategoryType.EXPENSE, "#EF4444", "🍔");
            createSystemCategory("Transportation", Category.CategoryType.EXPENSE, "#F59E0B", "🚗");
            createSystemCategory("Shopping", Category.CategoryType.EXPENSE, "#EC4899", "🛍️");
            createSystemCategory("Entertainment", Category.CategoryType.EXPENSE, "#8B5CF6", "🎬");
            createSystemCategory("Bills & Utilities", Category.CategoryType.EXPENSE, "#3B82F6", "💡");
            createSystemCategory("Healthcare", Category.CategoryType.EXPENSE, "#10B981", "⚕️");
            createSystemCategory("Education", Category.CategoryType.EXPENSE, "#F59E0B", "📚");
            createSystemCategory("Housing", Category.CategoryType.EXPENSE, "#6366F1", "🏠");
            createSystemCategory("Personal Care", Category.CategoryType.EXPENSE, "#EC4899", "💅");
            createSystemCategory("Other Expenses", Category.CategoryType.EXPENSE, "#6B7280", "📦");
            
            log.info("System categories initialized successfully");
        }
    }
    
    private void createSystemCategory(String name, Category.CategoryType type, String color, String icon) {
        Category category = Category.builder()
                .name(name)
                .type(type)
                .color(color)
                .icon(icon)
                .isSystem(true)
                .build();
        categoryRepository.save(category);
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
