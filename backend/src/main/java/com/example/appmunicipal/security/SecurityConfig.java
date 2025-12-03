package com.example.appmunicipal.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de seguridad de Spring Security
 * - Configura CORS para permitir peticiones desde el frontend
 * - Configura JWT para autenticación stateless
 * - Define qué endpoints requieren autenticación
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en los controllers
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        /**
         * Configuración principal de seguridad
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Deshabilitar CSRF (no es necesario para APIs REST con JWT)
                                .csrf(AbstractHttpConfigurer::disable)

                                // Configurar CORS
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                                // Configurar autorización de requests
                                .authorizeHttpRequests(auth -> auth
                                                // ============================================
                                                // ENDPOINTS PÚBLICOS (sin autenticación)
                                                // ============================================
                                                .requestMatchers(
                                                                "/api/usuarios/login",
                                                                "/api/usuarios/registro",
                                                                "/h2-console/**",
                                                                "/",
                                                                "/error")
                                                .permitAll()

                                                // ============================================
                                                // ENDPOINTS PROTEGIDOS
                                                // ============================================
                                                // Descomentar cuando quieras activar la seguridad
                                                // .requestMatchers("/api/denuncias/**").hasAnyRole("USUARIO",
                                                // "FUNCIONARIO",
                                                // "ADMIN")
                                                // .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                // .requestMatchers("/api/funcionario/**").hasAnyRole("FUNCIONARIO",
                                                // "ADMIN")

                                                // ============================================
                                                // POR AHORA: PERMITIR TODO (DESARROLLO)
                                                // ============================================
                                                .anyRequest().permitAll() // 👈 Cambiar a .authenticated() cuando
                                                                          // quieras activar seguridad
                                )

                                // Configurar sesión como STATELESS (sin estado, usando JWT)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Agregar el filtro JWT antes del filtro de autenticación estándar
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                                // Permitir frames para H2 Console
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.sameOrigin()));

                return http.build();
        }

        /**
         * Configuración de CORS para permitir peticiones desde el frontend
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // Orígenes permitidos (frontend)
                configuration.setAllowedOriginPatterns(Arrays.asList(
                                "http://localhost:4200",
                                "http://localhost:8090",
                                "http://127.0.0.1:4200",
                                "http://127.0.0.1:8090",
                                "http://localhost:3000",
                                "http://127.0.0.1:3000",
                                "http://200.13.5.5:8090",
                                "http://200.13.4.228:4200",
                                "http://200.13.4.228:8090",
                                "http://200.13.4.228:80",
                                "http://200.13.4.228:3000"));

                // Métodos HTTP permitidos
                configuration.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

                // Headers permitidos
                configuration.setAllowedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type",
                                "X-Requested-With",
                                "Accept",
                                "Origin",
                                "Access-Control-Request-Method",
                                "Access-Control-Request-Headers"));

                // Headers expuestos (que el frontend puede leer)
                configuration.setExposedHeaders(Arrays.asList(
                                "Authorization",
                                "Content-Type"));

                // Permitir credenciales (cookies, authorization headers)
                configuration.setAllowCredentials(true);

                // Tiempo de cache para preflight requests (OPTIONS)
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

        /**
         * Bean para encriptar contraseñas
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
