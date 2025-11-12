package com.financehub.dto;

import com.financehub.entity.Budget.BudgetPeriod;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating/updating a budget.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    
    @NotBlank(message = "Budget name is required")
    @Size(max = 100, message = "Budget name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @NotNull(message = "Budget period is required")
    private BudgetPeriod period;
    
    private Long categoryId;
    
    @DecimalMin(value = "0", message = "Alert threshold must be between 0 and 100")
    @DecimalMax(value = "100", message = "Alert threshold must be between 0 and 100")
    private BigDecimal alertThreshold = BigDecimal.valueOf(80);
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
