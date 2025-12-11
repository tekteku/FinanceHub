package com.financehub.service;

import com.financehub.dto.InvestorProfileDTO;
import com.financehub.entity.User;
import com.financehub.exception.ResourceNotFoundException;
import com.financehub.repository.InvestmentRepository;
import com.financehub.repository.ProjectReviewRepository;
import com.financehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InvestorProfileService {

    private final UserRepository userRepository;
    private final InvestmentRepository investmentRepository;
    private final ProjectReviewRepository reviewRepository;

    public InvestorProfileDTO getInvestorProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        InvestorProfileDTO profile = new InvestorProfileDTO();
        profile.setUserId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setFullName(user.getFullName());
        profile.setEmail(user.getEmail());
        profile.setCreatedAt(user.getCreatedAt().toString());

        // Calculate investment stats
        BigDecimal totalInvested = investmentRepository.findByInvestorId(userId).stream()
                .map(investment -> investment.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        profile.setTotalInvested(totalInvested);

        long projectsInvested = investmentRepository.findByInvestorId(userId).stream()
                .map(investment -> investment.getProject().getId())
                .distinct()
                .count();
        profile.setProjectsInvested((int) projectsInvested);

        // Get review stats
        long totalReviews = reviewRepository.findByInvestorId(userId).size();
        profile.setTotalReviews((int) totalReviews);

        if (totalReviews > 0) {
            Double avgRating = reviewRepository.findByInvestorId(userId).stream()
                    .mapToDouble(review -> review.getRating())
                    .average()
                    .orElse(0.0);
            profile.setAverageRating(avgRating);
        }

        return profile;
    }
}
