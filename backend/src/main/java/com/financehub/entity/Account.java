package com.financehub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a financial account (bank account, credit card, etc.).
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Entity
@Table(name = "accounts", indexes = {
    @Index(name = "idx_user_account", columnList = "user_id"),
    @Index(name = "idx_account_type", columnList = "type")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType type;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(length = 3)
    private String currency = "USD";
    
    @Column(length = 7)
    private String color;
    
    @Column(length = 50)
    private String icon;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(length = 500)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions = new HashSet<>();
    
    public enum AccountType {
        CHECKING,
        SAVINGS,
        CREDIT_CARD,
        INVESTMENT,
        CASH,
        LOAN,
        OTHER
    }
}
