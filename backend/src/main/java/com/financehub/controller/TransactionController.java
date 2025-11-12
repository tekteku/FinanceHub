package com.financehub.controller;

import com.financehub.dto.ApiResponse;
import com.financehub.dto.TransactionRequest;
import com.financehub.dto.TransactionResponse;
import com.financehub.entity.Transaction;
import com.financehub.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for transaction management.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Transaction management APIs")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TransactionController {
    
    private final TransactionService transactionService;
    
    @Operation(summary = "Get all transactions", description = "Retrieve all transactions with pagination")
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC)
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.debug("GET /api/transactions - Get all transactions");
        Page<TransactionResponse> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @Operation(summary = "Get transactions by type", description = "Retrieve transactions filtered by type")
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByType(
            @PathVariable Transaction.TransactionType type,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.debug("GET /api/transactions/type/{} - Get transactions by type", type);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByType(type, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @Operation(summary = "Get transactions by account", description = "Retrieve transactions for a specific account")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccount(
            @PathVariable Long accountId,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
        log.debug("GET /api/transactions/account/{} - Get transactions by account", accountId);
        Page<TransactionResponse> transactions = transactionService.getTransactionsByAccount(accountId, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    @Operation(summary = "Get recent transactions", description = "Retrieve the 10 most recent transactions")
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getRecentTransactions() {
        log.debug("GET /api/transactions/recent - Get recent transactions");
        List<TransactionResponse> transactions = transactionService.getRecentTransactions();
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    @Operation(summary = "Get transaction by ID", description = "Retrieve a specific transaction by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(@PathVariable Long id) {
        log.debug("GET /api/transactions/{} - Get transaction by ID", id);
        TransactionResponse transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success(transaction));
    }
    
    @Operation(summary = "Create transaction", description = "Create a new transaction")
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        log.debug("POST /api/transactions - Create new transaction");
        TransactionResponse transaction = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transaction created successfully", transaction));
    }
    
    @Operation(summary = "Update transaction", description = "Update an existing transaction")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        log.debug("PUT /api/transactions/{} - Update transaction", id);
        TransactionResponse transaction = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Transaction updated successfully", transaction));
    }
    
    @Operation(summary = "Delete transaction", description = "Delete a transaction")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        log.debug("DELETE /api/transactions/{} - Delete transaction", id);
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok(ApiResponse.success("Transaction deleted successfully", null));
    }
    
    @Operation(summary = "Get transactions by date range", description = "Retrieve transactions within a date range")
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("GET /api/transactions/range - Get transactions from {} to {}", startDate, endDate);
        List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }
    
    @Operation(summary = "Get total income", description = "Get total income for a period")
    @GetMapping("/income/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("GET /api/transactions/income/total - Get total income from {} to {}", startDate, endDate);
        BigDecimal total = transactionService.getTotalIncomeForPeriod(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(total));
    }
    
    @Operation(summary = "Get total expenses", description = "Get total expenses for a period")
    @GetMapping("/expenses/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("GET /api/transactions/expenses/total - Get total expenses from {} to {}", startDate, endDate);
        BigDecimal total = transactionService.getTotalExpensesForPeriod(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(total));
    }
}
