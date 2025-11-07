package com.financehub.config;
import com.financehub.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final AuthService authService;
    
    @Override
    public void run(ApplicationArguments args) {
        authService.createDefaultUser();
    }
}