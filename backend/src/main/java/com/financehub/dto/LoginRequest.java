package com.financehub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for user login.
 */
@Data
public class LoginRequest {
    @NotBlank(message = "Username or email is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}