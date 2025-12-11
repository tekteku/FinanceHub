package com.financehub.controller;

import com.financehub.dto.InvestorProfileDTO;
import com.financehub.entity.User;
import com.financehub.service.InvestorProfileService;
import com.financehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investor-profiles")
@RequiredArgsConstructor
public class InvestorProfileController {

    private final InvestorProfileService profileService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<InvestorProfileDTO> getMyProfile(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(profileService.getInvestorProfile(user.getId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<InvestorProfileDTO> getInvestorProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getInvestorProfile(userId));
    }
}
