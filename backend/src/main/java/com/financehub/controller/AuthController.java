package com.financehub.controller;

import com.financehub.dto.*;
import com.financehub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 * Handles user login, registration, and logout.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final AuthService authService;
    
    @Operation(summary = "User login", description = "Authenticate user with username/email and password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        log.info("User logged in successfully: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<com.financehub.dto.ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        log.info("User registered successfully: {}", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(com.financehub.dto.ApiResponse.success("User registered successfully", response));
    }
    
    @Operation(summary = "User logout", description = "Logout current user (client-side token removal)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully logged out")
    })
    @PostMapping("/logout")
    public ResponseEntity<com.financehub.dto.ApiResponse<Void>> logout() {
        log.info("User logout");
        return ResponseEntity.ok(com.financehub.dto.ApiResponse.success("Logged out successfully", null));
    }
}