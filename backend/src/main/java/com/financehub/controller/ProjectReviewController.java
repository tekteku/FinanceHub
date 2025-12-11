package com.financehub.controller;

import com.financehub.dto.ProjectReviewDTO;
import com.financehub.entity.User;
import com.financehub.service.ProjectReviewService;
import com.financehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ProjectReviewController {

    private final ProjectReviewService reviewService;
    private final UserService userService;

    @PostMapping("/projects/{projectId}")
    public ResponseEntity<ProjectReviewDTO> createReview(
            @PathVariable Long projectId,
            @RequestBody ProjectReviewDTO reviewDTO,
            Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(reviewService.createReview(projectId, user.getId(), reviewDTO));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ProjectReviewDTO>> getProjectReviews(@PathVariable Long projectId) {
        return ResponseEntity.ok(reviewService.getProjectReviews(projectId));
    }

    @GetMapping("/projects/{projectId}/rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long projectId) {
        return ResponseEntity.ok(reviewService.getAverageRating(projectId));
    }
}
