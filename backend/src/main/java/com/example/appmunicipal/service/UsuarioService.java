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
    private final Random random = new Random();

    /**
     * Login universal (móvil y web) con email y password
     * Retorna JWT token y datos básicos del usuario
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("🔐 Intento de login para: {}", request.getEmail());

        try {
            // Validar campos requeridos
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new LoginResponse(false, "El email es requerido", null, null, null, null);
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return new LoginResponse(false, "La contraseña es requerida", null, null, null, null);
            }

            // Buscar usuario por email
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("No existe una cuenta con este email"));

            // Verificar que el usuario esté activo
            if (!usuario.getActivo()) {
                return new LoginResponse(false, "Tu cuenta está inactiva. Contacta al administrador", null, null, null, null);
            }

            // Verificar contraseña (sin encriptar)
            if (!request.getPassword().equals(usuario.getPassword())) {
                log.warn("❌ Intento de login fallido - contraseña incorrecta para: {}", request.getEmail());
                return new LoginResponse(false, "Contraseña incorrecta", null, null, null, null);
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
                    usuario.getRol().getNombre()
            );

            log.info("✅ Login exitoso para: {} (ID: {}) - JWT Token generado", usuario.getEmail(), usuario.getId());

            // Retornar solo los datos necesarios
            return new LoginResponse(
                    true,
                    "Login exitoso",
                    token,
                    usuario.getEmail(),
                    usuario.getUsername(),
                    usuario.getNombre()
            );

        } catch (RuntimeException e) {
            log.error("❌ Error en login: {}", e.getMessage());
            return new LoginResponse(false, e.getMessage(), null, null, null, null);
        }
    }

    /**
     * Registrar un nuevo usuario
     *
     * REGLAS:
     * - RUT es OBLIGATORIO
     * - Email es OBLIGATORIO
     * - Nombre y Apellido son OBLIGATORIOS
     * - Password es OBLIGATORIO
     * - Username es OPCIONAL (se genera automáticamente)
     * - Telefono es OPCIONAL
     * - Siempre se registra como ACTIVO
     * - Siempre se asigna rol CIUDADANO
     */
    @Transactional
    public UsuarioResponse registrarUsuario(RegistroRequest request) {
        log.info("📝 Iniciando registro de usuario: {}", request.getEmail());

        // ========================================
        // VALIDACIONES DE CAMPOS OBLIGATORIOS
        // ========================================

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

        // ========================================
        // VALIDAR FORMATO Y DÍGITO VERIFICADOR DEL RUT
        // ========================================

        // Validar formato del RUT
        if (!rutUtil.validarFormato(request.getRut())) {
            throw new RuntimeException("El formato del RUT es inválido. Formato esperado: 12.345.678-9 o 12345678-9");
        }

        // Validar dígito verificador
        if (!rutUtil.validarDigitoVerificador(request.getRut())) {
            throw new RuntimeException("El RUT ingresado no es válido. Verifica el dígito verificador");
        }

        log.info("✅ RUT validado correctamente: {}", request.getRut());

        // Normalizar RUT para almacenamiento (sin puntos: "12345678-9")
        String rutNormalizado = rutUtil.normalizarRut(request.getRut());
        log.info("📝 RUT normalizado para almacenamiento: {}", rutNormalizado);

        // ========================================
        // VALIDAR QUE NO EXISTAN DUPLICADOS
        // ========================================

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe una cuenta con este email: " + request.getEmail());
        }

        if (usuarioRepository.existsByRut(rutNormalizado)) {
            throw new RuntimeException("Ya existe una cuenta con este RUT: " + rutUtil.formatearRut(rutNormalizado));
        }

        // ========================================
        // GENERAR USERNAME SI NO VIENE
        // ========================================

        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = generarUsername(request.getNombre(), request.getApellido());
            log.info("✨ Username generado automáticamente: {}", username);
        } else {
            if (usuarioRepository.existsByUsername(username)) {
                throw new RuntimeException("El username ya está en uso: " + username);
            }
        }

        // ========================================
        // CREAR NUEVO USUARIO
        // ========================================

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(request.getPassword());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setRut(rutNormalizado);  // ✅ Guardar RUT normalizado
        usuario.setActivo(true);

        // Asignar rol CIUDADANO
        Rol rolCiudadano = rolRepository.findByNombre(Rol.CIUDADANO)
                .orElseThrow(() -> new RuntimeException("Error crítico: Rol CIUDADANO no encontrado en la base de datos"));

        usuario.setRol(rolCiudadano);

        // ========================================
        // GUARDAR USUARIO
        // ========================================

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        log.info("✅ Usuario registrado exitosamente:");
        log.info("   - ID: {}", usuarioGuardado.getId());
        log.info("   - Username: {}", usuarioGuardado.getUsername());
        log.info("   - Email: {}", usuarioGuardado.getEmail());
        log.info("   - RUT: {}", usuarioGuardado.getRut());
        log.info("   - Rol: {}", usuarioGuardado.getRol().getNombre());
        log.info("   - Activo: {}", usuarioGuardado.getActivo());

        return new UsuarioResponse(usuarioGuardado);
    }

    /**
     * Generar username automático con formato: [inicial_nombre][inicial_apellido][numero_aleatorio]
     * Ejemplo: Juan Pérez -> jp12345
     */
    private String generarUsername(String nombre, String apellido) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre es requerido para generar el username");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new RuntimeException("El apellido es requerido para generar el username");
        }

        // Obtener iniciales en minúsculas
        String inicialNombre = nombre.trim().substring(0, 1).toLowerCase();
        String inicialApellido = apellido.trim().substring(0, 1).toLowerCase();
        String baseUsername = inicialNombre + inicialApellido;

        String username = null;
        int intentos = 0;
        int maxIntentos = 10;

        // Intentar generar username único
        while (intentos < maxIntentos) {
            int numeroDigitos = random.nextInt(3) + 3; // 3, 4 o 5 dígitos
            int numeroAleatorio = generarNumeroAleatorio(numeroDigitos);
            username = baseUsername + numeroAleatorio;

            if (!usuarioRepository.existsByUsername(username)) {
                log.info("✨ Username único generado: {} (intento {})", username, intentos + 1);
                return username;
            }

            intentos++;
            log.warn("⚠️  Username {} ya existe, reintentando... ({}/{})", username, intentos, maxIntentos);
        }

        // Fallback: usar timestamp si no se logró en 10 intentos
        username = baseUsername + System.currentTimeMillis() % 1000000;
        log.warn("⚠️  No se pudo generar username único en {} intentos, usando timestamp: {}", maxIntentos, username);

        return username;
    }

    /**
     * Generar número aleatorio con cantidad específica de dígitos
     */
    private int generarNumeroAleatorio(int digitos) {
        int min = (int) Math.pow(10, digitos - 1); // 100, 1000, 10000
        int max = (int) Math.pow(10, digitos) - 1;  // 999, 9999, 99999
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Validar token JWT
     */
    public boolean validarToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            boolean valido = jwtUtil.validarToken(token);

            if (valido) {
                // Verificar que el usuario aún exista y esté activo
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

    /**
     * Obtener ID de usuario desde el token JWT
     */
    public Long obtenerUsuarioIdDesdeToken(String token) {
        try {
            return jwtUtil.extraerUsuarioId(token);
        } catch (Exception e) {
            throw new RuntimeException("Token JWT inválido: " + e.getMessage());
        }
    }

    /**
     * Obtener información completa del token JWT
     */
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