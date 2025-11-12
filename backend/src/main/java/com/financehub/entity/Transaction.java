package com.financehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a financial transaction.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_account_transaction", columnList = "account_id"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_transaction_type", columnList = "type"),
    @Index(name = "idx_category_transaction", columnList = "category_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 200)
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_transaction_id")
    private RecurringTransaction recurringTransaction;
    
    @Column(length = 100)
    private String payee;
    
    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;
    
    public enum TransactionType {
        INCOME,
        EXPENSE,
        TRANSFER
    }
}
