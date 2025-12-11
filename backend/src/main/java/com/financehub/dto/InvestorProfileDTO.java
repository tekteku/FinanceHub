package com.financehub.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestorProfileDTO {
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private BigDecimal totalInvested;
    private Integer projectsInvested;
    private Double averageReturnRate;
    private Integer totalReviews;
    private Double averageRating;
    private String createdAt;
}
