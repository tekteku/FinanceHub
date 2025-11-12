package com.financehub.dto;

import com.financehub.entity.Account.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating/updating an account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    
    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Account type is required")
    private AccountType type;
    
    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Balance must be greater than 0")
    private BigDecimal balance;
    
    @Size(max = 3, message = "Currency code must be 3 characters")
    private String currency = "USD";
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code")
    private String color;
    
    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
