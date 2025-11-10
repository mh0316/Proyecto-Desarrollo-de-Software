package com.example.appmunicipal.service;

import com.example.appmunicipal.domain.Rol;
import com.example.appmunicipal.domain.Usuario;
import com.example.appmunicipal.DTO.LoginRequest;
import com.example.appmunicipal.DTO.LoginResponse;
import com.example.appmunicipal.DTO.RegistroRequest;
import com.example.appmunicipal.DTO.UsuarioResponse;
import com.example.appmunicipal.repository.RolRepository;
import com.example.appmunicipal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final Random random = new Random();

    /**
     * Login universal (móvil y web) con email y password
     * Retorna solo: token, email, username, nombre
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

            // Generar token de sesión
            String token = generarTokenSesion(usuario.getId());

            log.info("✅ Login exitoso para: {} (ID: {})", usuario.getEmail(), usuario.getId());

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
     * Generar token de sesión simple basado en UUID
     * Formato: userId-uuid-timestamp
     */
    private String generarTokenSesion(Long usuarioId) {
        String uuid = UUID.randomUUID().toString();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return usuarioId + "-" + uuid + "-" + timestamp;
    }

    /**
     * Validar token de sesión
     */
    public boolean validarToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            String[] partes = token.split("-");
            if (partes.length < 3) {
                return false;
            }

            Long usuarioId = Long.parseLong(partes[0]);

            // Verificar que el usuario exista y esté activo
            return usuarioRepository.findById(usuarioId)
                    .map(Usuario::getActivo)
                    .orElse(false);

        } catch (Exception e) {
            log.error("Error al validar token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtener ID de usuario desde el token
     */
    public Long obtenerUsuarioIdDesdeToken(String token) {
        try {
            String[] partes = token.split("-");
            return Long.parseLong(partes[0]);
        } catch (Exception e) {
            throw new RuntimeException("Token inválido");
        }
    }

    /**
     * Registrar un nuevo usuario
     */
    @Transactional
    public UsuarioResponse registrarUsuario(RegistroRequest request) {
        log.info("📝 Registrando nuevo usuario: {}", request.getEmail());

        // Validar que el email sea requerido
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email es requerido");
        }

        // Generar username si no viene en el request
        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = generarUsername(request.getNombre(), request.getApellido());
            log.info("Username generado automáticamente: {}", username);
        } else {
            // Validar que el username no exista
            if (usuarioRepository.existsByUsername(username)) {
                throw new RuntimeException("El username ya está en uso: " + username);
            }
        }

        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe una cuenta con este email: " + request.getEmail());
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(request.getPassword());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setRut(request.getRut());
        usuario.setActivo(true);

        // Asignar rol
        Rol rol;
        if (request.getRolId() != null) {
            rol = rolRepository.findById(request.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + request.getRolId()));
        } else {
            // Rol por defecto: CIUDADANO
            rol = rolRepository.findByNombre("CIUDADANO")
                    .orElseThrow(() -> new RuntimeException("Rol CIUDADANO no encontrado en la base de datos"));
        }
        usuario.setRol(rol);

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        log.info("✅ Usuario registrado exitosamente: ID={}, username={}",
                usuarioGuardado.getId(), usuarioGuardado.getUsername());

        return new UsuarioResponse(usuarioGuardado);
    }

    /**
     * Generar username automático con formato: [inicial_nombre][inicial_apellido][numero_aleatorio]
     */
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
                log.info("Username único generado: {} (intento {})", username, intentos + 1);
                return username;
            }

            intentos++;
        }

        username = baseUsername + System.currentTimeMillis() % 1000000;
        log.warn("Usando timestamp para username: {}", username);

        return username;
    }

    private int generarNumeroAleatorio(int digitos) {
        int min = (int) Math.pow(10, digitos - 1);
        int max = (int) Math.pow(10, digitos) - 1;
        return random.nextInt(max - min + 1) + min;
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