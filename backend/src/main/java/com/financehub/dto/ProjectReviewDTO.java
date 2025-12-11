package com.financehub.dto;

import lombok.Data;

@Data
public class ProjectReviewDTO {
    private Long id;
    private Long projectId;
    private Long investorId;
    private String investorName;
    private Integer rating;
    private String comment;
    private Boolean helpful;
    private String createdAt;
}
