package com.financehub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for monthly income and expense trends.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTrend {
    private String month;
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal balance;
}
