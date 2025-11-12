package com.financehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for category-wise expense breakdown.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpense {
    private String categoryName;
    private BigDecimal amount;
    private BigDecimal percentage;
}
