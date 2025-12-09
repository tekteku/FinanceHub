package com.financehub.dto;

import com.financehub.entity.ProjectStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private ProjectStatus status;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
}
