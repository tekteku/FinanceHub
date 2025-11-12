package com.financehub.dto;

import com.financehub.entity.Category.CategoryType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating a category.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Category type is required")
    private CategoryType type;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code")
    private String color;
    
    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
