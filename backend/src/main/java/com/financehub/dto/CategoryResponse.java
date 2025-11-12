package com.financehub.dto;

import com.financehub.entity.Category.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for category information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private CategoryType type;
    private String color;
    private String icon;
    private String description;
    private Boolean isSystem;
    private LocalDateTime createdAt;
}
