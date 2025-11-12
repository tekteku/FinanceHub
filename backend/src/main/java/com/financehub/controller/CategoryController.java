package com.financehub.controller;

import com.financehub.dto.ApiResponse;
import com.financehub.dto.CategoryRequest;
import com.financehub.dto.CategoryResponse;
import com.financehub.entity.Category;
import com.financehub.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for category management.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Category management APIs")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    @Operation(summary = "Get all categories", description = "Retrieve all categories (system + user's own)")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.debug("GET /api/categories - Get all categories");
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @Operation(summary = "Get categories by type", description = "Retrieve categories filtered by type")
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoriesByType(
            @PathVariable Category.CategoryType type) {
        log.debug("GET /api/categories/type/{} - Get categories by type", type);
        List<CategoryResponse> categories = categoryService.getCategoriesByType(type);
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        log.debug("GET /api/categories/{} - Get category by ID", id);
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
    
    @Operation(summary = "Create category", description = "Create a new custom category")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        log.debug("POST /api/categories - Create new category");
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }
    
    @Operation(summary = "Update category", description = "Update an existing custom category")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        log.debug("PUT /api/categories/{} - Update category", id);
        CategoryResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }
    
    @Operation(summary = "Delete category", description = "Delete a custom category")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        log.debug("DELETE /api/categories/{} - Delete category", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
