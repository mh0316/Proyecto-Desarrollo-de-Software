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
     * Registrar un nuevo funcionario
     * POST /api/usuarios/registro-funcionario
     */
    @PostMapping("/registro-funcionario")
    public ResponseEntity<?> registrarFuncionario(@RequestBody RegistroRequest request) {
        try {
            log.info("📝 Solicitud de registro de funcionario recibida: {}", request.getEmail());

            UsuarioResponse funcionario = usuarioService.registrarFuncionario(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Funcionario registrado exitosamente");
            response.put("funcionario", funcionario);

            log.info("✅ Funcionario registrado: {} (ID: {})", funcionario.getUsername(), funcionario.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al registrar funcionario: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Login universal (móvil y web) con email y password
     * POST /api/usuarios/login
     *
     * Retorna JWT Token en la respuesta
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("🔐 Solicitud de login recibida para: {}", request.getEmail());

        LoginResponse response = usuarioService.login(request);

        if (response.isSuccess()) {
            log.info("✅ Login exitoso - JWT Token generado para: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } else {
            log.warn("❌ Login fallido para: {} - Razón: {}", request.getEmail(), response.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Validar token JWT
     * GET /api/usuarios/sesion/validar?token=xxxxx
     */
    @GetMapping("/sesion/validar")
    public ResponseEntity<?> validarSesion(@RequestParam String token) {
        log.info("🔐 Validando token JWT");

        boolean valido = usuarioService.validarToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valido", valido);
        response.put("message", valido ? "Token JWT válido" : "Token JWT inválido o expirado");

        if (valido) {
            log.info("✅ Token JWT válido");
        } else {
            log.warn("❌ Token JWT inválido o expirado");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener información del token JWT
     * GET /api/usuarios/token/info
     * Header: Authorization: Bearer {token}
     */
    @GetMapping("/token/info")
    public ResponseEntity<?> obtenerInfoToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Token no proporcionado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String token = authHeader.substring(7); // Remover "Bearer "

            Map<String, Object> info = usuarioService.obtenerInfoToken(token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tokenInfo", info);

            log.info("✅ Información del token obtenida");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error al obtener info del token: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al decodificar token: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Obtener perfil del usuario desde el token JWT
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
                error.put("message", "Token JWT inválido o expirado");
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
     * Listar todos los usuarios
     * GET /api/usuarios/listar
     */
    @GetMapping("/listar")
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
}