package com.example.appmunicipal.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    // Clave secreta para firmar el JWT (en producción debe estar en
    // application.yml)
    private static final String SECRET_KEY = "municipal-app-secret-key-2024-super-secure-change-in-production-12345";

    // Tiempo de expiración del token: 24 horas (en milisegundos)
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000; // 24 horas

    /**
     * Generar token JWT para un usuario
     *
     * @param usuarioId ID del usuario
     * @param email     Email del usuario
     * @param username  Username del usuario
     * @param nombre    Nombre del usuario
     * @param rol       Rol del usuario
     * @return Token JWT firmado
     */
    public String generarToken(Long usuarioId, String email, String username, String nombre, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);
        claims.put("email", email);
        claims.put("username", username);
        claims.put("nombre", nombre);
        claims.put("rol", rol);

        return crearToken(claims, email);
    }

    /**
     * Crear el token JWT con los claims y el subject
     */
    private String crearToken(Map<String, Object> claims, String subject) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + JWT_TOKEN_VALIDITY);

        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key) // El algoritmo se infiere automáticamente de la clave
                .compact();
    }

    /**
     * Validar si el token es válido
     */
    public boolean validarToken(String token) {
        try {
            Claims claims = extraerTodosLosClaims(token);
            Date expiracion = claims.getExpiration();
            return !expiracion.before(new Date());
        } catch (Exception e) {
            log.error("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extraer el email (subject) del token
     */
    public String extraerEmail(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Extraer el ID del usuario del token
     */
    public Long extraerUsuarioId(String token) {
        Claims claims = extraerTodosLosClaims(token);
        Object usuarioId = claims.get("usuarioId");

        if (usuarioId instanceof Integer) {
            return ((Integer) usuarioId).longValue();
        } else if (usuarioId instanceof Long) {
            return (Long) usuarioId;
        }

        throw new RuntimeException("No se pudo extraer usuarioId del token");
    }

    /**
     * Extraer el username del token
     */
    public String extraerUsername(String token) {
        Claims claims = extraerTodosLosClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Extraer el nombre del token
     */
    public String extraerNombre(String token) {
        Claims claims = extraerTodosLosClaims(token);
        return claims.get("nombre", String.class);
    }

    /**
     * Extraer el rol del token
     */
    public String extraerRol(String token) {
        Claims claims = extraerTodosLosClaims(token);
        return claims.get("rol", String.class);
    }

    /**
     * Extraer la fecha de expiración del token
     */
    public Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    /**
     * Extraer un claim específico del token
     */
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extraer todos los claims del token
     */
    private Claims extraerTodosLosClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verificar si el token ha expirado
     */
    public boolean isTokenExpirado(String token) {
        try {
            return extraerExpiracion(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Obtener información completa del token
     */
    public Map<String, Object> obtenerInfoToken(String token) {
        Map<String, Object> info = new HashMap<>();
        try {
            Claims claims = extraerTodosLosClaims(token);

            info.put("usuarioId", claims.get("usuarioId"));
            info.put("email", claims.getSubject());
            info.put("username", claims.get("username"));
            info.put("nombre", claims.get("nombre"));
            info.put("rol", claims.get("rol"));
            info.put("expiracion", claims.getExpiration());
            info.put("emitido", claims.getIssuedAt());
            info.put("valido", !isTokenExpirado(token));

        } catch (Exception e) {
            log.error("Error al obtener info del token: {}", e.getMessage());
        }

        return info;
    }
}