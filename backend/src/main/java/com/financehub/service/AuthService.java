package com.financehub.service;

import com.financehub.dto.*;
import com.financehub.entity.User;
import com.financehub.exception.DuplicateResourceException;
import com.financehub.exception.UnauthorizedException;
import com.financehub.repository.UserRepository;
import com.financehub.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service for handling authentication operations.
 * Provides user login, registration, and token management.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    
    /**
     * Authenticate user and generate JWT token.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            log.debug("Authentication successful for user: {}", request.getUsername());
            
            var userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtUtils.generateToken(userDetails);
            
            var user = userRepository.findByUsername(request.getUsername())
                    .orElseGet(() -> userRepository.findByEmail(request.getUsername())
                            .orElseThrow(() -> new UnauthorizedException("User not found")));
            
            return new AuthResponse(token, user.getId(), user.getUsername(), 
                    user.getEmail(), user.getFullName(), user.getRoles());
                    
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        } catch (DisabledException e) {
            log.error("Account disabled for user: {}", request.getUsername());
            throw new UnauthorizedException("Account is disabled");
        } catch (LockedException e) {
            log.error("Account locked for user: {}", request.getUsername());
            throw new UnauthorizedException("Account is locked");
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsername(), e);
            throw new UnauthorizedException("Authentication failed");
        }
    }
    
    /**
     * Register a new user account.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Registering new user: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(Set.of("ROLE_USER"));
        user.setIsActive(true);
        
        user = userRepository.save(user);
        log.info("New user registered successfully: {}", user.getUsername());
        
        // Generate token for auto-login
        var userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtils.generateToken(userDetails);
        
        return new AuthResponse(token, user.getId(), user.getUsername(),
                user.getEmail(), user.getFullName(), user.getRoles());
    }
    
    /**
     * Create default admin user if not exists.
     */
    @Transactional
    public void createDefaultUser() {
        if (userRepository.findByUsername("admin@financehub.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin@financehub.com");
            admin.setEmail("admin@financehub.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setFullName("Admin User");
            admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
            admin.setIsActive(true);
            userRepository.save(admin);
            log.info("Default admin user created");
        }
    }
}