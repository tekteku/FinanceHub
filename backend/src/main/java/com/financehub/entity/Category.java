package com.financehub.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a transaction category.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Entity
@Table(name = "categories", indexes = {
    @Index(name = "idx_category_type", columnList = "type"),
    @Index(name = "idx_user_category", columnList = "user_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoryType type;
    
    @Column(length = 7)
    private String color;
    
    @Column(length = 50)
    private String icon;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    public enum CategoryType {
        INCOME,
        EXPENSE
    }
}
