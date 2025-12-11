package com.financehub.repository;

import com.financehub.entity.ProjectReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectReviewRepository extends JpaRepository<ProjectReview, Long> {
    List<ProjectReview> findByProjectId(Long projectId);
    List<ProjectReview> findByInvestorId(Long investorId);
    
    @Query("SELECT AVG(pr.rating) FROM ProjectReview pr WHERE pr.project.id = :projectId")
    Double getAverageRating(@Param("projectId") Long projectId);
}
