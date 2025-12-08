package com.example.appmunicipal.security;

import com.example.appmunicipal.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro que intercepta cada request HTTP para validar el token JWT
 * y establecer la autenticación en el contexto de Spring Security
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Extraer el header Authorization
        final String authHeader = request.getHeader("Authorization");

        // 2. Validar que el header exista y tenga el formato correcto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No se encontró token JWT en el request a: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extraer el token (remover "Bearer " del inicio)
            final String jwt = authHeader.substring(7);

            // 4. Validar el token
            if (!jwtUtil.validarToken(jwt)) {
                log.warn("Token JWT inválido o expirado");
                filterChain.doFilter(request, response);
                return;
            }

            // 5. Extraer información del token
            final String email = jwtUtil.extraerEmail(jwt);
            final String rol = jwtUtil.extraerRol(jwt);

            // 6. Si no hay autenticación previa, establecerla
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Crear la autoridad basada en el rol
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);

                // Crear el objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, // Principal (identificador del usuario)
                        null, // Credentials (no necesitamos la contraseña aquí)
                        Collections.singletonList(authority) // Authorities (roles)
                );

                // Agregar detalles adicionales del request
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Usuario autenticado: {} con rol: {}", email, rol);
            }

        } catch (Exception e) {
            log.error("Error al procesar el token JWT: {}", e.getMessage());
        }

        // 7. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
