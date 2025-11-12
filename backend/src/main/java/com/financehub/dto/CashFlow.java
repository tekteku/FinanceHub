package com.financehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for cash flow analysis.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashFlow {
    private BigDecimal openingBalance;
    private BigDecimal totalInflows;
    private BigDecimal totalOutflows;
    private BigDecimal closingBalance;
}
