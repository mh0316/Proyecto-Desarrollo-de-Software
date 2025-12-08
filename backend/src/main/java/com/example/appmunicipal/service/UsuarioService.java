package com.example.appmunicipal.service;

import com.example.appmunicipal.domain.Rol;
import com.example.appmunicipal.domain.Usuario;
import com.example.appmunicipal.DTO.LoginRequest;
import com.example.appmunicipal.DTO.LoginResponse;
import com.example.appmunicipal.DTO.RegistroRequest;
import com.example.appmunicipal.DTO.UsuarioResponse;
import com.example.appmunicipal.repository.RolRepository;
import com.example.appmunicipal.repository.UsuarioRepository;
import com.example.appmunicipal.util.JwtUtil;
import com.example.appmunicipal.util.RutUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final JwtUtil jwtUtil;
    private final RutUtil rutUtil;
    // 🔒 PasswordEncoder para encriptar contraseñas
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    /**
     * Login universal - Retorna solo token y email
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("🔐 Intento de login para: {}", request.getEmail());

        try {
            // Validar campos requeridos
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new LoginResponse(false, "El email es requerido", null, null);
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return new LoginResponse(false, "La contraseña es requerida", null, null);
            }

            // Buscar usuario por email
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("No existe una cuenta con este email"));

            // Verificar que el usuario esté activo
            if (!usuario.getActivo()) {
                return new LoginResponse(false, "Tu cuenta está inactiva. Contacta al administrador", null, null);
            }

            // 🔒 Verificar contraseña con BCrypt
            if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                log.warn("❌ Intento de login fallido - contraseña incorrecta para: {}", request.getEmail());
                return new LoginResponse(false, "Contraseña incorrecta", null, null);
            }

            // Actualizar última conexión
            usuario.setUltimaConexion(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // Generar token JWT
            String token = jwtUtil.generarToken(
                    usuario.getId(),
                    usuario.getEmail(),
                    usuario.getUsername(),
                    usuario.getNombre(),
                    usuario.getRol().getNombre());

            log.info("✅ Login exitoso para: {} (ID: {})", usuario.getEmail(), usuario.getId());

            // Retornar SOLO token y email (seguro para mantener sesión)
            return new LoginResponse(
                    true,
                    "Login exitoso",
                    token,
                    usuario.getEmail());

        } catch (RuntimeException e) {
            log.error("❌ Error en login: {}", e.getMessage());
            return new LoginResponse(false, e.getMessage(), null, null);
        }
    }

    /**
     * Registro de usuarios o funcionario segun parametro ingresado por
     * UsuarioController
     */
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request, String rolNombre) {
        log.info("📝 Iniciando registro de {}: {}", rolNombre, request.getEmail());

        // Validaciones
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        if (request.getApellido() == null || request.getApellido().trim().isEmpty()) {
            throw new RuntimeException("El apellido es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            throw new RuntimeException("El RUT es obligatorio");
        }

        // Validar formato RUT
        // if (!rutUtil.validarFormato(request.getRut())) {
        // throw new RuntimeException("El formato del RUT es inválido. Formato esperado:
        // 12.345.678-9 o 12345678-9");
        // }

        // if (!rutUtil.validarDigitoVerificador(request.getRut())) {
        // throw new RuntimeException("El RUT ingresado no es válido. Verifica el dígito
        // verificador");
        // }

        String rutNormalizado = rutUtil.normalizarRut(request.getRut());

        // Validar duplicados
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe una cuenta con este email: " + request.getEmail());
        }

        if (usuarioRepository.existsByRut(rutNormalizado)) {
            throw new RuntimeException("Ya existe una cuenta con este RUT: " + rutUtil.formatearRut(rutNormalizado));
        }

        // Username
        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = generarUsername(request.getNombre(), request.getApellido());
            log.info("✨ Username generado automáticamente: {}", username);
        } else {
            if (usuarioRepository.existsByUsername(username)) {
                throw new RuntimeException("El username ya está en uso: " + username);
            }
        }

        // Crear Usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(username);

        // 🔒 Guardar contraseña hasheada con BCrypt
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setRut(rutNormalizado);
        usuario.setActivo(true);

        // Rol dinámico
        Rol rol = rolRepository.findByNombre(rolNombre)
                .orElseThrow(() -> new RuntimeException("Error crítico: Rol " + rolNombre + " no encontrado"));

        usuario.setRol(rol);

        // Guardar
        Usuario guardado = usuarioRepository.save(usuario);

        log.info("✅ Registro exitoso [{}]: {}", rolNombre, guardado.getUsername());

        return new UsuarioResponse(guardado);
    }

    private String generarUsername(String nombre, String apellido) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre es requerido para generar el username");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new RuntimeException("El apellido es requerido para generar el username");
        }

        String inicialNombre = nombre.trim().substring(0, 1).toLowerCase();
        String inicialApellido = apellido.trim().substring(0, 1).toLowerCase();
        String baseUsername = inicialNombre + inicialApellido;

        String username = null;
        int intentos = 0;
        int maxIntentos = 10;

        while (intentos < maxIntentos) {
            int numeroDigitos = random.nextInt(3) + 3;
            int numeroAleatorio = generarNumeroAleatorio(numeroDigitos);
            username = baseUsername + numeroAleatorio;

            if (!usuarioRepository.existsByUsername(username)) {
                log.info("✨ Username único generado: {} (intento {})", username, intentos + 1);
                return username;
            }

            intentos++;
        }

        username = baseUsername + System.currentTimeMillis() % 1000000;
        log.warn("⚠️ Usando timestamp para username: {}", username);

        return username;
    }

    private int generarNumeroAleatorio(int digitos) {
        int min = (int) Math.pow(10, digitos - 1);
        int max = (int) Math.pow(10, digitos) - 1;
        return random.nextInt(max - min + 1) + min;
    }

    // Métodos auxiliares...

    public boolean validarToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            boolean valido = jwtUtil.validarToken(token);

            if (valido) {
                Long usuarioId = jwtUtil.extraerUsuarioId(token);
                return usuarioRepository.findById(usuarioId)
                        .map(Usuario::getActivo)
                        .orElse(false);
            }

            return false;

        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage());
            return false;
        }
    }

    public Long obtenerUsuarioIdDesdeToken(String token) {
        try {
            return jwtUtil.extraerUsuarioId(token);
        } catch (Exception e) {
            throw new RuntimeException("Token JWT inválido: " + e.getMessage());
        }
    }

    public java.util.Map<String, Object> obtenerInfoToken(String token) {
        return jwtUtil.obtenerInfoToken(token);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {
        log.info("📋 Listando todos los usuarios");
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.info("🔍 Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return new UsuarioResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorUsername(String username) {
        log.info("🔍 Buscando usuario por username: {}", username);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return new UsuarioResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorEmail(String email) {
        log.info("🔍 Buscando usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));
        return new UsuarioResponse(usuario);
    }
}