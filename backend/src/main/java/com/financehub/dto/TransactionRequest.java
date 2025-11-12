package com.financehub.dto;

import com.financehub.entity.Transaction.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating/updating a transaction.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction type is required")
    private TransactionType type;
    
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Size(max = 200, message = "Notes must not exceed 200 characters")
    private String notes;
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    private Long categoryId;
    
    @Size(max = 100, message = "Payee must not exceed 100 characters")
    private String payee;
}
