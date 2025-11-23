// src/main/java/com/example/appmunicipal/config/WebConfig.java

package com.example.appmunicipal.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Divide la cadena de orígenes por comas
        String[] origins = allowedOrigins.split(",");

        registry.addMapping("/api/**")          // Solo aplica a tus endpoints de API
                .allowedOriginPatterns(origins) // Usa patterns para mayor flexibilidad
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept")
                .allowCredentials(false);       // false porque usas localStorage, no cookies
    }
}