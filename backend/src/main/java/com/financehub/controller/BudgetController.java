package com.financehub.controller;

import com.financehub.dto.ApiResponse;
import com.financehub.dto.BudgetRequest;
import com.financehub.dto.BudgetResponse;
import com.financehub.security.JwtTokenProvider;
import com.financehub.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Budget management operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Budget Management", description = "APIs for managing budgets and expense tracking")
@SecurityRequirement(name = "bearerAuth")
public class BudgetController {
    
    private final BudgetService budgetService;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * Get all budgets for the authenticated user
     */
    @GetMapping
    @Operation(summary = "Get all budgets", description = "Retrieve all budgets for the authenticated user")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getAllBudgets(
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        List<BudgetResponse> budgets = budgetService.getAllBudgets(userId);
        
        return ResponseEntity.ok(ApiResponse.<List<BudgetResponse>>builder()
                .success(true)
                .message("Budgets retrieved successfully")
                .data(budgets)
                .build());
    }
    
    /**
     * Get active budgets for the authenticated user
     */
    @GetMapping("/active")
    @Operation(summary = "Get active budgets", description = "Retrieve only active budgets")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getActiveBudgets(
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        List<BudgetResponse> budgets = budgetService.getActiveBudgets(userId);
        
        return ResponseEntity.ok(ApiResponse.<List<BudgetResponse>>builder()
                .success(true)
                .message("Active budgets retrieved successfully")
                .data(budgets)
                .build());
    }
    
    /**
     * Get budget by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get budget by ID", description = "Retrieve a specific budget by its ID")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudgetById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        BudgetResponse budget = budgetService.getBudgetById(id, userId);
        
        return ResponseEntity.ok(ApiResponse.<BudgetResponse>builder()
                .success(true)
                .message("Budget retrieved successfully")
                .data(budget)
                .build());
    }
    
    /**
     * Create a new budget
     */
    @PostMapping
    @Operation(summary = "Create budget", description = "Create a new budget for expense tracking")
    public ResponseEntity<ApiResponse<BudgetResponse>> createBudget(
            @Valid @RequestBody BudgetRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        BudgetResponse budget = budgetService.createBudget(request, userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<BudgetResponse>builder()
                        .success(true)
                        .message("Budget created successfully")
                        .data(budget)
                        .build());
    }
    
    /**
     * Update an existing budget
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update budget", description = "Update an existing budget")
    public ResponseEntity<ApiResponse<BudgetResponse>> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        BudgetResponse budget = budgetService.updateBudget(id, request, userId);
        
        return ResponseEntity.ok(ApiResponse.<BudgetResponse>builder()
                .success(true)
                .message("Budget updated successfully")
                .data(budget)
                .build());
    }
    
    /**
     * Delete a budget
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete budget", description = "Delete a budget")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        budgetService.deleteBudget(id, userId);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Budget deleted successfully")
                .build());
    }
    
    /**
     * Get budgets exceeding threshold
     */
    @GetMapping("/alerts")
    @Operation(summary = "Get budget alerts", description = "Retrieve budgets that have exceeded their alert threshold")
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgetAlerts(
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        List<BudgetResponse> budgets = budgetService.getBudgetsExceedingThreshold(userId);
        
        return ResponseEntity.ok(ApiResponse.<List<BudgetResponse>>builder()
                .success(true)
                .message("Budget alerts retrieved successfully")
                .data(budgets)
                .build());
    }
}
