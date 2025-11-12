package com.financehub.controller;

import com.financehub.dto.AccountRequest;
import com.financehub.dto.AccountResponse;
import com.financehub.dto.ApiResponse;
import com.financehub.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for account management.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Accounts", description = "Account management APIs")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class AccountController {
    
    private final AccountService accountService;
    
    @Operation(summary = "Get all accounts", description = "Retrieve all accounts for the current user")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAllAccounts() {
        log.debug("GET /api/accounts - Get all accounts");
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }
    
    @Operation(summary = "Get active accounts", description = "Retrieve only active accounts")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getActiveAccounts() {
        log.debug("GET /api/accounts/active - Get active accounts");
        List<AccountResponse> accounts = accountService.getActiveAccounts();
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }
    
    @Operation(summary = "Get account by ID", description = "Retrieve a specific account by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable Long id) {
        log.debug("GET /api/accounts/{} - Get account by ID", id);
        AccountResponse account = accountService.getAccountById(id);
        return ResponseEntity.ok(ApiResponse.success(account));
    }
    
    @Operation(summary = "Create account", description = "Create a new account")
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            @Valid @RequestBody AccountRequest request) {
        log.debug("POST /api/accounts - Create new account");
        AccountResponse account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", account));
    }
    
    @Operation(summary = "Update account", description = "Update an existing account")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequest request) {
        log.debug("PUT /api/accounts/{} - Update account", id);
        AccountResponse account = accountService.updateAccount(id, request);
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully", account));
    }
    
    @Operation(summary = "Delete account", description = "Delete an account (soft delete)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        log.debug("DELETE /api/accounts/{} - Delete account", id);
        accountService.deleteAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
    
    @Operation(summary = "Get total balance", description = "Get total balance across all active accounts")
    @GetMapping("/balance/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalBalance() {
        log.debug("GET /api/accounts/balance/total - Get total balance");
        BigDecimal total = accountService.getTotalBalance();
        return ResponseEntity.ok(ApiResponse.success(total));
    }
}
