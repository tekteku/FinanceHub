package com.financehub.service;
import com.financehub.dto.*;
import com.financehub.entity.User;
import com.financehub.repository.UserRepository;
import com.financehub.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        var userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtils.generateToken(userDetails);
        
        var user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> userRepository.findByEmail(request.getUsername()).orElseThrow());
        
        return new AuthResponse(token, user.getId(), user.getUsername(), 
                user.getEmail(), user.getFullName(), user.getRoles());
    }
    
    public void createDefaultUser() {
        if (userRepository.findByUsername("admin@financehub.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin@financehub.com");
            admin.setEmail("admin@financehub.com");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setFullName("Admin User");
            admin.setRoles(Set.of("ROLE_ADMIN"));
            userRepository.save(admin);
        }
    }
}