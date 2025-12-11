package com.financehub.service;

import com.financehub.dto.ProjectReviewDTO;
import com.financehub.entity.ProjectReview;
import com.financehub.entity.User;
import com.financehub.entity.Project;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.repository.ProjectReviewRepository;
import com.financehub.repository.ProjectRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectReviewService {

    private final ProjectReviewRepository reviewRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectReviewDTO createReview(Long projectId, Long userId, ProjectReviewDTO reviewDTO) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        
        User investor = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProjectReview review = new ProjectReview();
        review.setProject(project);
        review.setInvestor(investor);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        ProjectReview savedReview = reviewRepository.save(review);
        return mapToDTO(savedReview);
    }

    public List<ProjectReviewDTO> getProjectReviews(Long projectId) {
        return reviewRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long projectId) {
        return reviewRepository.getAverageRating(projectId);
    }

    private ProjectReviewDTO mapToDTO(ProjectReview review) {
        ProjectReviewDTO dto = new ProjectReviewDTO();
        dto.setId(review.getId());
        dto.setProjectId(review.getProject().getId());
        dto.setInvestorId(review.getInvestor().getId());
        dto.setInvestorName(review.getInvestor().getFullName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setHelpful(review.getHelpful());
        dto.setCreatedAt(review.getCreatedAt().toString());
        return dto;
    }
}
