package com.example.appmunicipal.service;

import com.example.appmunicipal.domain.Categoria;
import com.example.appmunicipal.domain.Denuncia;
import com.example.appmunicipal.domain.Usuario;
import com.example.appmunicipal.domain.Evidencia;
import com.example.appmunicipal.DTO.DenunciaRequest;
import com.example.appmunicipal.DTO.DenunciaResponse;
import com.example.appmunicipal.DTO.EvidenciaResponse;
import com.example.appmunicipal.repository.CategoriaRepository;
import com.example.appmunicipal.repository.DenunciaRepository;
import com.example.appmunicipal.repository.UsuarioRepository;
import com.example.appmunicipal.repository.EvidenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import com.example.appmunicipal.DTO.DashboardStatsResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class DenunciaService {

    private final DenunciaRepository denunciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final EvidenciaRepository evidenciaRepository;
    private final com.example.appmunicipal.repository.NotificacionRepository notificacionRepository;

    /**
     * Crear una nueva denuncia
     * El usuario se identifica por su email
     *
     * @param request Datos de la denuncia incluyendo el email del usuario
     * @return DenunciaResponse con los datos de la denuncia creada
     */
    @Transactional
    public DenunciaResponse crearDenuncia(DenunciaRequest request) {
        log.info("📝 Iniciando creación de denuncia");

        // ========================================
        // VALIDAR Y OBTENER USUARIO POR EMAIL
        // ========================================

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email del usuario es obligatorio");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No existe un usuario con el email: " + request.getEmail()));

        if (!usuario.getActivo()) {
            throw new RuntimeException("Tu cuenta está inactiva. Contacta al administrador");
        }

        log.info("👤 Denuncia creada por: {} (ID: {}, Email: {})",
                usuario.getUsername(), usuario.getId(), usuario.getEmail());

        // ========================================
        // VALIDAR CAMPOS OBLIGATORIOS
        // ========================================

        if (request.getCategoriaId() == null) {
            throw new RuntimeException("La categoría es obligatoria");
        }

        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción es obligatoria");
        }

        if (request.getLatitud() == null) {
            throw new RuntimeException("La latitud es obligatoria");
        }

        if (request.getLongitud() == null) {
            throw new RuntimeException("La longitud es obligatoria");
        }

        // Validar rangos de coordenadas (Chile continental)
        if (request.getLatitud() < -56 || request.getLatitud() > -17) {
            throw new RuntimeException("La latitud está fuera del rango válido para Chile");
        }

        if (request.getLongitud() < -76 || request.getLongitud() > -66) {
            throw new RuntimeException("La longitud está fuera del rango válido para Chile");
        }

        // ========================================
        // VALIDAR Y OBTENER CATEGORÍA
        // ========================================

        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + request.getCategoriaId()));

        if (!categoria.getActiva()) {
            throw new RuntimeException("La categoría seleccionada no está disponible");
        }

        log.info("📂 Categoría: {} ({})", categoria.getNombre(), categoria.getCodigo());

        // ========================================
        // CREAR DENUNCIA
        // ========================================

        Denuncia denuncia = new Denuncia();
        denuncia.setUsuario(usuario);
        denuncia.setCategoria(categoria);
        denuncia.setDescripcion(request.getDescripcion().trim());
        denuncia.setPatente(request.getPatente() != null ? request.getPatente().trim().toUpperCase() : null);
        denuncia.setLatitud(request.getLatitud());
        denuncia.setLongitud(request.getLongitud());
        denuncia.setDireccion(request.getDireccion());
        denuncia.setSector(request.getSector());
        denuncia.setComuna(request.getComuna() != null ? request.getComuna() : "Temuco"); // Por defecto Temuco
        denuncia.setEstado(Denuncia.EstadoDenuncia.PENDIENTE);

        // ========================================
        // GUARDAR DENUNCIA
        // ========================================

        Denuncia denunciaGuardada = denunciaRepository.save(denuncia);

        log.info("✅ Denuncia creada exitosamente:");
        log.info("   - ID: {}", denunciaGuardada.getId());
        log.info("   - Usuario: {} ({})", usuario.getUsername(), usuario.getEmail());
        log.info("   - Categoría: {}", categoria.getNombre());
        log.info("   - Estado: {}", denunciaGuardada.getEstado());
        log.info("   - Ubicación: {}, {}", denunciaGuardada.getLatitud(), denunciaGuardada.getLongitud());

        return new DenunciaResponse(denunciaGuardada);
    }

    /**
     * Obtener una denuncia por ID
     *
     * @param id ID de la denuncia
     * @return DenunciaResponse con los datos de la denuncia
     */
    @Transactional(readOnly = true)
    public DenunciaResponse obtenerDenunciaPorId(Long id) {
        log.info("🔍 Buscando denuncia con ID: {}", id);

        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + id));

        log.info("✅ Denuncia encontrada: {} - {}", denuncia.getId(), denuncia.getCategoria().getNombre());

        return new DenunciaResponse(denuncia);
    }

    /**
     * Listar todas las denuncias (ordenadas por fecha descendente)
     *
     * @return Lista de DenunciaResponse
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarTodasLasDenuncias() {
        log.info("📋 Listando todas las denuncias");

        List<Denuncia> denuncias = denunciaRepository.findAllByOrderByFechaDenunciaDesc();

        log.info("✅ {} denuncias encontradas", denuncias.size());

        return denuncias.stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Listar denuncias paginadas (ordenadas por fecha descendente)
     *
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     * @return Map con denuncias y metadata de paginación
     */
    @Transactional(readOnly = true)
    public Map<String, Object> listarDenunciasPaginadas(int page, int size) {
        log.info("📋 Listando denuncias paginadas - Página: {}, Tamaño: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Denuncia> denunciasPage = denunciaRepository.findAllByOrderByFechaDenunciaDesc(pageable);

        List<DenunciaResponse> denuncias = denunciasPage.getContent().stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("denuncias", denuncias);
        response.put("currentPage", denunciasPage.getNumber());
        response.put("totalPages", denunciasPage.getTotalPages());
        response.put("totalElements", denunciasPage.getTotalElements());
        response.put("pageSize", denunciasPage.getSize());
        response.put("hasNext", denunciasPage.hasNext());
        response.put("hasPrevious", denunciasPage.hasPrevious());

        log.info("✅ Página {}/{} - {} denuncias en esta página, {} total",
                denunciasPage.getNumber() + 1,
                denunciasPage.getTotalPages(),
                denuncias.size(),
                denunciasPage.getTotalElements());

        return response;
    }

    /**
     * Listar denuncias de un usuario específico por email
     *
     * @param email Email del usuario
     * @return Lista de DenunciaResponse
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarDenunciasPorEmail(String email) {
        log.info("📋 Listando denuncias del usuario con email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));

        List<Denuncia> denuncias = denunciaRepository.findByUsuarioIdOrderByFechaDenunciaDesc(usuario.getId());

        log.info("✅ {} denuncias encontradas para el usuario", denuncias.size());

        return denuncias.stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Listar denuncias de un usuario específico por ID
     *
     * @param usuarioId ID del usuario
     * @return Lista de DenunciaResponse
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarDenunciasPorUsuario(Long usuarioId) {
        log.info("📋 Listando denuncias del usuario ID: {}", usuarioId);

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuarioId);
        }

        List<Denuncia> denuncias = denunciaRepository.findByUsuarioIdOrderByFechaDenunciaDesc(usuarioId);

        log.info("✅ {} denuncias encontradas para el usuario", denuncias.size());

        return denuncias.stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Listar denuncias por estado
     *
     * @param estado Estado de la denuncia (PENDIENTE, VALIDADA, RECHAZADA, etc.)
     * @return Lista de DenunciaResponse
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarDenunciasPorEstado(String estado) {
        log.info("📋 Listando denuncias con estado: {}", estado);

        try {
            Denuncia.EstadoDenuncia estadoDenuncia = Denuncia.EstadoDenuncia.valueOf(estado.toUpperCase());

            List<Denuncia> denuncias = denunciaRepository.findByEstado(estadoDenuncia);

            log.info("✅ {} denuncias encontradas con estado {}", denuncias.size(), estado);

            return denuncias.stream()
                    .map(DenunciaResponse::new)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + estado
                    + ". Estados válidos: PENDIENTE, EN_REVISION, VALIDADA, RECHAZADA, CERRADA");
        }
    }

    /**
     * Listar denuncias por categoría
     *
     * @param categoriaId ID de la categoría
     * @return Lista de DenunciaResponse
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarDenunciasPorCategoria(Long categoriaId) {
        log.info("📋 Listando denuncias de la categoría ID: {}", categoriaId);

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + categoriaId);
        }

        List<Denuncia> denuncias = denunciaRepository.findByCategoriaId(categoriaId);

        log.info("✅ {} denuncias encontradas para la categoría", denuncias.size());

        return denuncias.stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Listar denuncias por sector
     *
     * @param sector Nombre del sector
     * @return Lista de DenunciaResponse
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarDenunciasPorSector(String sector) {
        log.info("📋 Listando denuncias del sector: {}", sector);

        List<Denuncia> denuncias = denunciaRepository.findBySector(sector);

        log.info("✅ {} denuncias encontradas en el sector", denuncias.size());

        return denuncias.stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Contar denuncias por estado
     *
     * @param estado Estado de la denuncia
     * @return Cantidad de denuncias
     */
    @Transactional(readOnly = true)
    public Long contarDenunciasPorEstado(String estado) {
        try {
            Denuncia.EstadoDenuncia estadoDenuncia = Denuncia.EstadoDenuncia.valueOf(estado.toUpperCase());
            return denunciaRepository.countByEstado(estadoDenuncia);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + estado);
        }
    }

    /**
     * Contar denuncias de un usuario
     *
     * @param usuarioId ID del usuario
     * @return Cantidad de denuncias
     */
    @Transactional(readOnly = true)
    public Long contarDenunciasPorUsuario(Long usuarioId) {
        return denunciaRepository.countByUsuarioId(usuarioId);
    }

    /**
     * Contar denuncias de un usuario por email
     *
     * @param email Email del usuario
     * @return Cantidad de denuncias
     */
    @Transactional(readOnly = true)
    public Long contarDenunciasPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return denunciaRepository.countByUsuarioId(usuario.getId());
    }

    /**
     * Obtener todas las evidencias de una denuncia
     *
     * @param denunciaId ID de la denuncia
     * @return Lista de EvidenciaResponse
     */
    @Transactional(readOnly = true)
    public List<EvidenciaResponse> obtenerEvidenciasPorDenuncia(Long denunciaId) {
        log.info("📋 Obteniendo evidencias para denuncia ID: {}", denunciaId);

        // Verificar que la denuncia existe
        if (!denunciaRepository.existsById(denunciaId)) {
            throw new RuntimeException("Denuncia no encontrada con ID: " + denunciaId);
        }

        List<Evidencia> evidencias = evidenciaRepository.findByDenunciaId(denunciaId);

        log.info("✅ {} evidencias encontradas", evidencias.size());

        return evidencias.stream()
                .map(EvidenciaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtener recurso de evidencia (imagen)
     *
     * @param filename Nombre del archivo
     * @return Resource del archivo
     */
    public Resource obtenerEvidencia(String filename) {
        try {
            // Determinar la ruta correcta del archivo
            // En desarrollo: backend/src/main/resources/static/uploads
            // En producción (Docker): /app/uploads
            String uploadPath = System.getenv("UPLOAD_PATH") != null
                    ? System.getenv("UPLOAD_PATH")
                    : "backend/src/main/resources/static/uploads";

            java.nio.file.Path filePath = java.nio.file.Paths.get(uploadPath).resolve(filename);
            Resource resource = new org.springframework.core.io.FileSystemResource(filePath);

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Fallback: intentar classpath por si acaso (para archivos antiguos/legacy)
                Resource classpathResource = new ClassPathResource("static/uploads/" + filename);
                if (classpathResource.exists() && classpathResource.isReadable()) {
                    return classpathResource;
                }

                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar evidencia: " + filename, e);
        }

    }

    /**
     * Subir evidencia para una denuncia
     *
     * @param denunciaId ID de la denuncia
     * @param archivo    Archivo de evidencia (foto/video)
     * @return EvidenciaResponse
     */
    @Transactional
    public EvidenciaResponse subirEvidencia(Long denunciaId, org.springframework.web.multipart.MultipartFile archivo) {
        log.info("📤 Subiendo evidencia para denuncia ID: {}", denunciaId);

        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + denunciaId));

        try {
            // 1. Validar archivo
            if (archivo.isEmpty()) {
                throw new RuntimeException("El archivo está vacío");
            }

            // 2. Generar nombre único
            String extension = obtenerExtension(archivo.getOriginalFilename());
            String nombreArchivo = "evidencia-" + System.currentTimeMillis() + extension;

            // 3. Guardar archivo en disco
            // En desarrollo: backend/src/main/resources/static/uploads
            // En producción (Docker): /app/uploads (volumen compartido)
            String uploadPath = System.getenv("UPLOAD_PATH") != null
                    ? System.getenv("UPLOAD_PATH")
                    : "backend/src/main/resources/static/uploads";

            java.nio.file.Path uploadDir = java.nio.file.Paths.get(uploadPath);
            if (!java.nio.file.Files.exists(uploadDir)) {
                java.nio.file.Files.createDirectories(uploadDir);
            }

            java.nio.file.Path filePath = uploadDir.resolve(nombreArchivo);
            java.nio.file.Files.copy(archivo.getInputStream(), filePath,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 4. Crear entidad Evidencia
            Evidencia evidencia = new Evidencia();
            evidencia.setDenuncia(denuncia);
            evidencia.setNombreArchivo(nombreArchivo);
            evidencia.setRutaArchivo("/uploads/" + nombreArchivo); // Ruta relativa para acceso web
            evidencia.setMimeType(archivo.getContentType());
            evidencia.setTamanoBytes(archivo.getSize());

            // Determinar tipo
            String mimeType = archivo.getContentType();
            if (mimeType != null && mimeType.startsWith("video")) {
                evidencia.setTipo(Evidencia.TipoEvidencia.VIDEO);
            } else {
                evidencia.setTipo(Evidencia.TipoEvidencia.FOTO);
            }

            // 5. Guardar en BD
            Evidencia evidenciaGuardada = evidenciaRepository.save(evidencia);

            log.info("✅ Evidencia guardada: {}", nombreArchivo);

            return new EvidenciaResponse(evidenciaGuardada);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    private String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // Default
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * Eliminar una denuncia y sus evidencias asociadas
     *
     * @param id ID de la denuncia a eliminar
     */
    @Transactional
    public void eliminarDenuncia(Long id) {
        log.info("🗑️ Eliminando denuncia ID: {}", id);

        Denuncia denuncia = denunciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + id));

        // 1. Eliminar archivos de evidencia del disco
        List<Evidencia> evidencias = evidenciaRepository.findByDenunciaId(id);
        String uploadPath = System.getenv("UPLOAD_PATH") != null
                ? System.getenv("UPLOAD_PATH")
                : "backend/src/main/resources/static/uploads";

        for (Evidencia evidencia : evidencias) {
            try {
                java.nio.file.Path filePath = java.nio.file.Paths.get(uploadPath, evidencia.getNombreArchivo());
                if (java.nio.file.Files.exists(filePath)) {
                    java.nio.file.Files.delete(filePath);
                    log.info("📁 Archivo eliminado: {}", evidencia.getNombreArchivo());
                }
            } catch (IOException e) {
                log.warn("⚠️ No se pudo eliminar el archivo: {} - {}", evidencia.getNombreArchivo(), e.getMessage());
            }
        }

        // 2. Eliminar notificaciones asociadas
        List<com.example.appmunicipal.domain.Notificacion> notificaciones = notificacionRepository.findByDenunciaId(id);
        if (!notificaciones.isEmpty()) {
            notificacionRepository.deleteAll(notificaciones);
            log.info("🔔 {} notificaciones eliminadas", notificaciones.size());
        }

        // 3. Eliminar denuncia (cascade eliminará evidencias, comentarios e historial
        // de BD)
        denunciaRepository.delete(denuncia);

        log.info("✅ Denuncia {} eliminada correctamente", id);
    }

    /**
     * Obtener estadísticas avanzadas para el dashboard
     */
    @Transactional(readOnly = true)
    public DashboardStatsResponse obtenerEstadisticasAvanzadas() {
        log.info("📊 Calculando estadísticas avanzadas");

        List<Denuncia> todasLasDenuncias = denunciaRepository.findAll();

        DashboardStatsResponse stats = new DashboardStatsResponse();

        // 1. Tendencia mensual (últimos 6 meses o todo el año)
        Map<String, Long> denunciasPorMes = todasLasDenuncias.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getFechaDenuncia().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()));
        stats.setDenunciasPorMes(new LinkedHashMap<>(denunciasPorMes)); // Convertir a LinkedHashMap si se necesita
                                                                        // orden específico después

        // 2. Denuncias por categoría
        Map<String, Long> denunciasPorCategoria = todasLasDenuncias.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCategoria().getNombre(),
                        Collectors.counting()));
        stats.setDenunciasPorCategoria(denunciasPorCategoria);

        // 3. Tasa de validación vs rechazo
        long totalCerradas = todasLasDenuncias.stream()
                .filter(d -> d.getEstado() == Denuncia.EstadoDenuncia.VALIDADA ||
                        d.getEstado() == Denuncia.EstadoDenuncia.RECHAZADA)
                .count();

        if (totalCerradas > 0) {
            long validadas = todasLasDenuncias.stream()
                    .filter(d -> d.getEstado() == Denuncia.EstadoDenuncia.VALIDADA)
                    .count();
            long rechazadas = todasLasDenuncias.stream()
                    .filter(d -> d.getEstado() == Denuncia.EstadoDenuncia.RECHAZADA)
                    .count();

            stats.setTasaValidacion((double) validadas / totalCerradas * 100);
            stats.setTasaRechazo((double) rechazadas / totalCerradas * 100);
        } else {
            stats.setTasaValidacion(0.0);
            stats.setTasaRechazo(0.0);
        }

        // 4. Tiempo promedio de validación (en horas)
        Double promedioHoras = todasLasDenuncias.stream()
                .filter(d -> d.getFechaValidacion() != null)
                .mapToLong(d -> ChronoUnit.HOURS.between(d.getFechaDenuncia(), d.getFechaValidacion()))
                .average()
                .orElse(0.0);
        stats.setTiempoPromedioValidacion(promedioHoras);

        // 5. Tendencias por horario (0-23 horas)
        Map<Integer, Long> denunciasPorHorario = todasLasDenuncias.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getFechaDenuncia().getHour(),
                        Collectors.counting()));
        stats.setDenunciasPorHorario(denunciasPorHorario);

        // 6. E9: Denuncias por Comuna
        Map<String, Long> denunciasPorComuna = todasLasDenuncias.stream()
                .filter(d -> d.getComuna() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getComuna().toUpperCase(),
                        Collectors.counting()));
        stats.setDenunciasPorComuna(denunciasPorComuna);

        // 7. E8: Denuncias por Sector (Solo Temuco)
        Map<String, Long> denunciasPorSector = todasLasDenuncias.stream()
                .filter(d -> d.getComuna() != null && "TEMUCO".equalsIgnoreCase(d.getComuna().trim()))
                .filter(d -> d.getSector() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getSector().toUpperCase(),
                        Collectors.counting()));
        stats.setDenunciasPorSector(denunciasPorSector);

        stats.setDenunciasPorSector(denunciasPorSector);

        // 8. T4: Tabla por usuario denunciante
        Map<String, Long> topUsuarios = todasLasDenuncias.stream()
                .filter(d -> d.getUsuario() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getUsuario().getNombre() + " " + d.getUsuario().getApellido() + " ("
                                + d.getUsuario().getEmail() + ")",
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(20) // Top 20
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        stats.setTopUsuarios(topUsuarios);

        // 9. E10: Reincidencia por patente
        Map<String, Long> reincidenciaPatentes = todasLasDenuncias.stream()
                .filter(d -> d.getPatente() != null && !d.getPatente().trim().isEmpty())
                .collect(Collectors.groupingBy(
                        d -> d.getPatente().toUpperCase().trim(),
                        Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 1) // Solo si hay reincidencia (> 1)
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(20) // Top 20
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
        stats.setReincidenciaPatentes(reincidenciaPatentes);

        return stats;
    }
}