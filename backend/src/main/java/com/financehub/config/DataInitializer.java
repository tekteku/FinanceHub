package com.financehub.config;

import com.financehub.service.AuthService;
import com.financehub.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.*;
import org.springframework.stereotype.Component;

/**
 * Initialize default data on application startup.
 * 
 * @author tekteku
 * @version 1.0
 * @since 2025-11-09
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {
    
    private final AuthService authService;
    private final CategoryService categoryService;
    
    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing application data...");
        authService.createDefaultUser();
        categoryService.initializeSystemCategories();
        log.info("Application data initialized successfully");
    }
}