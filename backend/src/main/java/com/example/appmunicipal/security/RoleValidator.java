package com.example.appmunicipal.security;

import com.example.appmunicipal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para verificar roles de usuario desde el token JWT
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleValidator {

    private final JwtUtil jwtUtil;

    /**
     * Extrae el token del header Authorization
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return Token JWT sin el prefijo "Bearer "
     */
    public String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    /**
     * Obtiene el rol del usuario desde el token JWT
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return Rol del usuario (CIUDADANO, FUNCIONARIO, ADMIN) o null si no hay
     *         token
     */
    public String obtenerRolDesdeToken(String authHeader) {
        try {
            String token = extraerToken(authHeader);
            if (token == null) {
                log.debug("No se encontró token en el header Authorization");
                return null;
            }

            if (!jwtUtil.validarToken(token)) {
                log.warn("Token JWT inválido o expirado");
                return null;
            }

            String rol = jwtUtil.extraerRol(token);
            log.info("🔑 Rol extraído del token: {}", rol);
            return rol;

        } catch (Exception e) {
            log.error("❌ Error al extraer rol del token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el email del usuario desde el token JWT
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return Email del usuario o null si no hay token
     */
    public String obtenerEmailDesdeToken(String authHeader) {
        try {
            String token = extraerToken(authHeader);
            if (token == null) {
                return null;
            }

            if (!jwtUtil.validarToken(token)) {
                return null;
            }

            return jwtUtil.extraerEmail(token);

        } catch (Exception e) {
            log.error("❌ Error al extraer email del token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene el ID del usuario desde el token JWT
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return ID del usuario o null si no hay token
     */
    public Long obtenerUsuarioIdDesdeToken(String authHeader) {
        try {
            String token = extraerToken(authHeader);
            if (token == null) {
                return null;
            }

            if (!jwtUtil.validarToken(token)) {
                return null;
            }

            return jwtUtil.extraerUsuarioId(token);

        } catch (Exception e) {
            log.error("❌ Error al extraer usuario ID del token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si el usuario tiene el rol de FUNCIONARIO
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return true si el usuario es FUNCIONARIO o ADMIN, false en caso contrario
     */
    public boolean esFuncionario(String authHeader) {
        String rol = obtenerRolDesdeToken(authHeader);
        boolean resultado = "FUNCIONARIO".equals(rol) || "ADMIN".equals(rol);

        log.info("🔍 Verificación de rol FUNCIONARIO: {} (Rol actual: {})", resultado, rol);

        return resultado;
    }

    /**
     * Verifica si el usuario tiene el rol de ADMIN
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return true si el usuario es ADMIN, false en caso contrario
     */
    public boolean esAdmin(String authHeader) {
        String rol = obtenerRolDesdeToken(authHeader);
        boolean resultado = "ADMIN".equals(rol);

        log.info("🔍 Verificación de rol ADMIN: {} (Rol actual: {})", resultado, rol);

        return resultado;
    }

    /**
     * Verifica si el usuario tiene el rol de CIUDADANO
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return true si el usuario es CIUDADANO, false en caso contrario
     */
    public boolean esCiudadano(String authHeader) {
        String rol = obtenerRolDesdeToken(authHeader);
        boolean resultado = "CIUDADANO".equals(rol);

        log.info("🔍 Verificación de rol CIUDADANO: {} (Rol actual: {})", resultado, rol);

        return resultado;
    }

    /**
     * Verifica si el usuario tiene alguno de los roles especificados
     * 
     * @param authHeader      Header Authorization completo (Bearer xxx)
     * @param rolesPermitidos Roles permitidos (CIUDADANO, FUNCIONARIO, ADMIN)
     * @return true si el usuario tiene alguno de los roles, false en caso contrario
     */
    public boolean tieneAlgunRol(String authHeader, String... rolesPermitidos) {
        String rolUsuario = obtenerRolDesdeToken(authHeader);

        if (rolUsuario == null) {
            return false;
        }

        for (String rolPermitido : rolesPermitidos) {
            if (rolPermitido.equals(rolUsuario)) {
                log.info("✅ Usuario tiene rol permitido: {}", rolUsuario);
                return true;
            }
        }

        log.warn("❌ Usuario con rol {} no tiene permisos. Roles permitidos: {}",
                rolUsuario, String.join(", ", rolesPermitidos));
        return false;
    }

    /**
     * Verifica si el usuario tiene rol de FUNCIONARIO y devuelve un ResponseEntity
     * de error si no lo tiene
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return ResponseEntity con error 403 si no es FUNCIONARIO, o null si tiene
     *         acceso
     */
    public ResponseEntity<?> verificarRolFuncionario(String authHeader) {
        String rol = obtenerRolDesdeToken(authHeader);
        String email = obtenerEmailDesdeToken(authHeader);

        if (!esFuncionario(authHeader)) {
            log.warn("❌ Acceso denegado: El usuario no es FUNCIONARIO (Rol actual: {})", rol);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Acceso denegado: Solo funcionarios pueden acceder a este recurso");
            error.put("rolRequerido", "FUNCIONARIO o ADMIN");
            error.put("rolActual", rol);
            error.put("email", email);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        // Si tiene acceso, retorna null (indica que puede continuar)
        return null;
    }

    /**
     * Verifica si el usuario tiene rol de ADMIN y devuelve un ResponseEntity de
     * error si no lo tiene
     * 
     * @param authHeader Header Authorization completo (Bearer xxx)
     * @return ResponseEntity con error 403 si no es ADMIN, o null si tiene acceso
     */
    public ResponseEntity<?> verificarRolAdmin(String authHeader) {
        String rol = obtenerRolDesdeToken(authHeader);
        String email = obtenerEmailDesdeToken(authHeader);

        if (!esAdmin(authHeader)) {
            log.warn("❌ Acceso denegado: El usuario no es ADMIN (Rol actual: {})", rol);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Acceso denegado: Solo administradores pueden acceder a este recurso");
            error.put("rolRequerido", "ADMIN");
            error.put("rolActual", rol);
            error.put("email", email);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return null;
    }

    /**
     * Verifica si el usuario tiene alguno de los roles especificados y devuelve un
     * ResponseEntity de error si no lo tiene
     * 
     * @param authHeader      Header Authorization completo (Bearer xxx)
     * @param rolesPermitidos Roles permitidos (CIUDADANO, FUNCIONARIO, ADMIN)
     * @return ResponseEntity con error 403 si no tiene ninguno de los roles, o null
     *         si tiene acceso
     */
    public ResponseEntity<?> verificarRoles(String authHeader, String... rolesPermitidos) {
        String rol = obtenerRolDesdeToken(authHeader);
        String email = obtenerEmailDesdeToken(authHeader);

        if (!tieneAlgunRol(authHeader, rolesPermitidos)) {
            log.warn("❌ Acceso denegado: El usuario no tiene los roles requeridos (Rol actual: {})", rol);

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Acceso denegado: No tiene los permisos necesarios");
            error.put("rolesRequeridos", String.join(", ", rolesPermitidos));
            error.put("rolActual", rol);
            error.put("email", email);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return null;
    }
}
