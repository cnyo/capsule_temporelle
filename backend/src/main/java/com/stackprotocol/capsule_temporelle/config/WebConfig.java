package com.stackprotocol.capsule_temporelle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for Cross-Origin Resource Sharing (CORS).
 * This configuration allows the frontend application to communicate with the backend API.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed.origins:http://localhost:4200,http://127.0.0.1:4200,http://localhost:9004,http://127.0.0.1:9004}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
