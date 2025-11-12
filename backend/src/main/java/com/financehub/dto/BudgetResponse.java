package com.financehub.dto;

import com.financehub.entity.Budget.BudgetPeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for budget information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetResponse {
    private Long id;
    private String name;
    private BigDecimal amount;
    private BigDecimal spent;
    private BigDecimal remaining;
    private BigDecimal spentPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private BudgetPeriod period;
    private Long categoryId;
    private String categoryName;
    private BigDecimal alertThreshold;
    private Boolean isAlertTriggered;
    private Boolean isActive;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
