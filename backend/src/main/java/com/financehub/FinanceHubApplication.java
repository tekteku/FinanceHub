package com.financehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FinanceHubApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FinanceHubApplication.class, args);
        System.out.println("\n🚀 FinanceHub Backend is running!");
        System.out.println("📚 Swagger UI: http://localhost:8080/api/swagger-ui.html");
        System.out.println("🔌 API Docs: http://localhost:8080/api/v3/api-docs\n");
    }
}
