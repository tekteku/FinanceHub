package com.financehub.repository;

import com.financehub.entity.Project;
import com.financehub.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByOwnerId(Long ownerId);

    @Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE' ORDER BY p.currentAmount DESC LIMIT 10")
    List<Project> findTrendingProjects();

    @Query("SELECT p FROM Project p WHERE p.status = 'ACTIVE' AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Project> searchProjects(@Param("keyword") String keyword);
}
