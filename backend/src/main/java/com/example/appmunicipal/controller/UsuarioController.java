package com.example.appmunicipal.controller;

import com.example.appmunicipal.DTO.*;
import com.example.appmunicipal.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Login universal (móvil y web) con email y password
     * POST /api/usuarios/login
     *
     * Retorna:
     * - success: true/false
     * - message: mensaje descriptivo
     * - token: token de sesión (si success = true)
     * - email: email del usuario
     * - username: username del usuario
     * - nombre: nombre del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("🔐 Solicitud de login recibida para: {}", request.getEmail());

        LoginResponse response = usuarioService.login(request);

        if (response.isSuccess()) {
            log.info("✅ Login exitoso - Token generado para: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } else {
            log.warn("❌ Login fallido para: {} - Razón: {}", request.getEmail(), response.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Validar token de sesión
     * GET /api/usuarios/sesion/validar?token=xxxxx
     */
    @GetMapping("/sesion/validar")
    public ResponseEntity<?> validarSesion(@RequestParam String token) {
        log.info("🔐 Validando token de sesión");

        boolean valido = usuarioService.validarToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valido", valido);
        response.put("message", valido ? "Sesión válida" : "Sesión inválida o expirada");

        if (valido) {
            log.info("✅ Token válido");
        } else {
            log.warn("❌ Token inválido o expirado");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener datos completos del usuario actual desde el token
     * GET /api/usuarios/sesion/perfil
     * Header: Authorization: Bearer {token}
     */
    @GetMapping("/sesion/perfil")
    public ResponseEntity<?> obtenerPerfilDesdeToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Token no proporcionado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String token = authHeader.substring(7); // Remover "Bearer "

            if (!usuarioService.validarToken(token)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Token inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Long usuarioId = usuarioService.obtenerUsuarioIdDesdeToken(token);
            UsuarioResponse usuario = usuarioService.buscarPorId(usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario);

            log.info("✅ Perfil obtenido para usuario ID: {}", usuarioId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error al obtener perfil: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener perfil: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Registrar un nuevo usuario
     * POST /api/usuarios/registro
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        try {
            log.info("📝 Solicitud de registro recibida: {}", request.getEmail());

            UsuarioResponse usuario = usuarioService.registrarUsuario(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("usuario", usuario);

            log.info("✅ Usuario registrado: {} (ID: {})", usuario.getUsername(), usuario.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al registrar usuario: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Listar todos los usuarios
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        try {
            List<UsuarioResponse> usuarios = usuarioService.listarUsuarios();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", usuarios.size());
            response.put("usuarios", usuarios);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error al listar usuarios: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener usuarios: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Buscar usuario por ID
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            UsuarioResponse usuario = usuarioService.buscarPorId(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al buscar usuario: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Buscar usuario por email
     * GET /api/usuarios/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        try {
            UsuarioResponse usuario = usuarioService.buscarPorEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuario", usuario);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al buscar usuario: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}