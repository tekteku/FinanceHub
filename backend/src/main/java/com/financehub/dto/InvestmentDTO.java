package com.financehub.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvestmentDTO {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private Long investorId;
    private String investorName;
    private BigDecimal amount;
    private LocalDateTime investedAt;
}
