package com.financehub.controller;

import com.financehub.dto.InvestmentDTO;
import com.financehub.entity.User;
import com.financehub.service.InvestmentService;
import com.financehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<InvestmentDTO> invest(@RequestBody Map<String, Object> payload, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Long projectId = Long.valueOf(payload.get("projectId").toString());
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        Long accountId = Long.valueOf(payload.get("accountId").toString());
        
        return ResponseEntity.ok(investmentService.invest(projectId, amount, user.getId(), accountId));
    }

    @GetMapping("/my-investments")
    public ResponseEntity<List<InvestmentDTO>> getMyInvestments(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(investmentService.getMyInvestments(user.getId()));
    }

    @GetMapping("/total-invested")
    public ResponseEntity<BigDecimal> getTotalInvested(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(investmentService.getTotalInvestedAmount(user.getId()));
    }
}
