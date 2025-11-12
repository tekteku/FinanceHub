package com.financehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a budget for expense tracking and management.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Entity
@Table(name = "budgets", indexes = {
    @Index(name = "idx_user_budget", columnList = "user_id"),
    @Index(name = "idx_budget_period", columnList = "start_date, end_date"),
    @Index(name = "idx_category_budget", columnList = "category_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal spent = BigDecimal.ZERO;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BudgetPeriod period;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "alert_threshold", precision = 5, scale = 2)
    private BigDecimal alertThreshold = BigDecimal.valueOf(80);
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(length = 500)
    private String description;
    
    public enum BudgetPeriod {
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY,
        CUSTOM
    }
    
    /**
     * Calculate the percentage of budget spent.
     */
    @Transient
    public BigDecimal getSpentPercentage() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spent.divide(amount, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
    
    /**
     * Check if budget alert threshold has been reached.
     */
    @Transient
    public Boolean isAlertTriggered() {
        return getSpentPercentage().compareTo(alertThreshold) >= 0;
    }
}
