package com.financehub.controller;

import com.financehub.dto.*;
import com.financehub.security.JwtTokenProvider;
import com.financehub.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for Analytics and reporting operations.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "APIs for financial analytics and reporting")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * Get financial summary for a date range
     */
    @GetMapping("/summary")
    @Operation(summary = "Get financial summary", description = "Get income, expenses, and balance for a date range")
    public ResponseEntity<ApiResponse<AnalyticsSummary>> getFinancialSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        AnalyticsSummary summary = analyticsService.getFinancialSummary(userId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.<AnalyticsSummary>builder()
                .success(true)
                .message("Financial summary retrieved successfully")
                .data(summary)
                .build());
    }
    
    /**
     * Get expenses by category
     */
    @GetMapping("/expenses/by-category")
    @Operation(summary = "Get expenses by category", description = "Get category-wise expense breakdown")
    public ResponseEntity<ApiResponse<List<CategoryExpense>>> getExpensesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        List<CategoryExpense> expenses = analyticsService.getExpensesByCategory(userId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.<List<CategoryExpense>>builder()
                .success(true)
                .message("Expenses by category retrieved successfully")
                .data(expenses)
                .build());
    }
    
    /**
     * Get monthly trends
     */
    @GetMapping("/trends/monthly")
    @Operation(summary = "Get monthly trends", description = "Get monthly income and expense trends")
    public ResponseEntity<ApiResponse<List<MonthlyTrend>>> getMonthlyTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        List<MonthlyTrend> trends = analyticsService.getMonthlyTrends(userId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.<List<MonthlyTrend>>builder()
                .success(true)
                .message("Monthly trends retrieved successfully")
                .data(trends)
                .build());
    }
    
    /**
     * Get cash flow analysis
     */
    @GetMapping("/cashflow")
    @Operation(summary = "Get cash flow", description = "Get cash flow analysis for a date range")
    public ResponseEntity<ApiResponse<CashFlow>> getCashFlow(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        CashFlow cashFlow = analyticsService.getCashFlow(userId, startDate, endDate);
        
        return ResponseEntity.ok(ApiResponse.<CashFlow>builder()
                .success(true)
                .message("Cash flow retrieved successfully")
                .data(cashFlow)
                .build());
    }
    
    /**
     * Get dashboard statistics (last 30 days)
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard stats", description = "Get quick statistics for dashboard (last 30 days)")
    public ResponseEntity<ApiResponse<AnalyticsSummary>> getDashboardStats(
            @RequestHeader("Authorization") String token) {
        Long userId = tokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        AnalyticsSummary stats = analyticsService.getDashboardStats(userId);
        
        return ResponseEntity.ok(ApiResponse.<AnalyticsSummary>builder()
                .success(true)
                .message("Dashboard statistics retrieved successfully")
                .data(stats)
                .build());
    }
}
