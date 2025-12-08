package com.example.appmunicipal.controller;

import com.example.appmunicipal.DTO.*;
import com.example.appmunicipal.security.RoleValidator;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.appmunicipal.service.AdministracionDenunciaService;
import com.example.appmunicipal.service.DenunciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/denuncias")
@RequiredArgsConstructor
@Slf4j
public class DenunciaController {

    private final DenunciaService denunciaService;
    private final RoleValidator roleValidator;
    private final AdministracionDenunciaService adminService;

    /**
     * Crear una nueva denuncia
     * POST /api/denuncias
     *
     * NO requiere Bearer Token
     * El usuario se identifica por el email en el body
     */
    @PostMapping
    public ResponseEntity<?> crearDenuncia(@RequestBody DenunciaRequest request) {
        try {
            log.info("📝 Solicitud de creación de denuncia recibida para email: {}", request.getEmail());

            DenunciaResponse denuncia = denunciaService.crearDenuncia(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Denuncia creada exitosamente");
            response.put("denuncia", denuncia);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al crear denuncia: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Obtener una denuncia por ID
     * GET /api/denuncias/{id}
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDenuncia(@PathVariable Long id) {
        try {
            DenunciaResponse denuncia = denunciaService.obtenerDenunciaPorId(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("denuncia", denuncia);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al obtener denuncia: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Listar todas las denuncias
     * GET /api/denuncias
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @GetMapping
    public ResponseEntity<?> listarDenuncias() {
        try {
            log.info("Solicitud de listar denuncias");
            List<DenunciaResponse> denuncias = denunciaService.listarTodasLasDenuncias();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok()
                    .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(response);

        } catch (Exception e) {
            log.error("❌ Error al listar denuncias: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al obtener denuncias: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Listar denuncias de un usuario por email
     * GET /api/denuncias/mis-denuncias?email=usuario@email.com
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/mis-denuncias")
    public ResponseEntity<?> listarMisDenuncias(@RequestParam String email) {
        try {
            log.info("📋 Listando denuncias para email: {}", email);

            List<DenunciaResponse> denuncias = denunciaService.listarDenunciasPorEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("email", email);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al listar denuncias por email: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Listar denuncias por estado
     * GET /api/denuncias/estado/{estado}
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> listarPorEstado(@PathVariable String estado) {
        try {
            List<DenunciaResponse> denuncias = denunciaService.listarDenunciasPorEstado(estado);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("estado", estado.toUpperCase());
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al listar denuncias por estado: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Listar denuncias por categoría
     * GET /api/denuncias/categoria/{categoriaId}
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<?> listarPorCategoria(@PathVariable Long categoriaId) {
        try {
            List<DenunciaResponse> denuncias = denunciaService.listarDenunciasPorCategoria(categoriaId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categoriaId", categoriaId);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al listar denuncias por categoría: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Listar denuncias por sector
     * GET /api/denuncias/sector/{sector}
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/sector/{sector}")
    public ResponseEntity<?> listarPorSector(@PathVariable String sector) {
        try {
            List<DenunciaResponse> denuncias = denunciaService.listarDenunciasPorSector(sector);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sector", sector);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error al listar denuncias por sector: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Listar denuncias de un usuario específico por ID
     * GET /api/denuncias/usuario/{usuarioId}
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<DenunciaResponse> denuncias = denunciaService.listarDenunciasPorUsuario(usuarioId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("usuarioId", usuarioId);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al listar denuncias por usuario: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Obtener estadísticas de denuncias
     * GET /api/denuncias/estadisticas
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 🔍 Extraer y mostrar el rol del token
            String rol = roleValidator.obtenerRolDesdeToken(authHeader);

            log.info("📊 Solicitud de estadísticas");
            log.info("🔑 Rol del usuario: {}", rol != null ? rol : "SIN TOKEN");

            // 🔒 VERIFICACIÓN DE ROL (COMENTADA - ACTIVAR CUANDO SEA NECESARIO)
            // ResponseEntity<?> errorResponse =
            // roleValidator.verificarRolFuncionario(authHeader);
            // if (errorResponse != null) {
            // return errorResponse;
            // }

            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("total", denunciaService.listarTodasLasDenuncias().size());
            estadisticas.put("pendientes", denunciaService.contarDenunciasPorEstado("PENDIENTE"));
            estadisticas.put("validadas", denunciaService.contarDenunciasPorEstado("VALIDADA"));
            estadisticas.put("rechazadas", denunciaService.contarDenunciasPorEstado("RECHAZADA"));
            estadisticas.put("en_revision", denunciaService.contarDenunciasPorEstado("EN_REVISION"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("estadisticas", estadisticas);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error al obtener estadísticas: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Contar denuncias de un usuario por email
     * GET /api/denuncias/contar?email=usuario@email.com
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/contar")
    public ResponseEntity<?> contarDenuncias(@RequestParam String email) {
        try {
            Long cantidad = denunciaService.contarDenunciasPorEmail(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("email", email);
            response.put("cantidad", cantidad);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al contar denuncias: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Obtener todas las evidencias de una denuncia
     * GET /api/denuncias/{id}/evidencias
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @GetMapping("/{id}/evidencias")
    public ResponseEntity<?> obtenerEvidenciasDenuncia(@PathVariable Long id) {
        try {
            List<com.example.appmunicipal.DTO.EvidenciaResponse> evidencias = denunciaService
                    .obtenerEvidenciasPorDenuncia(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("denunciaId", id);
            response.put("count", evidencias.size());
            response.put("evidencias", evidencias);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al obtener evidencias: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Subir evidencia para una denuncia
     * POST /api/denuncias/{id}/evidencias
     */
    @PreAuthorize("hasAnyRole('CIUDADANO', 'FUNCIONARIO')")
    @PostMapping("/{id}/evidencias")
    public ResponseEntity<?> subirEvidencia(
            @PathVariable Long id,
            @RequestParam("archivo") org.springframework.web.multipart.MultipartFile archivo) {
        try {
            com.example.appmunicipal.DTO.EvidenciaResponse evidencia = denunciaService.subirEvidencia(id, archivo);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Evidencia subida correctamente");
            response.put("evidencia", evidencia);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al subir evidencia: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Obtener imagen de evidencia
     * GET /api/denuncias/evidencia/{filename}
     */
    @GetMapping("/evidencia/{filename:.+}")
    public ResponseEntity<Resource> obtenerEvidencia(@PathVariable String filename) {
        try {
            Resource file = denunciaService.obtenerEvidencia(filename);

            String contentType = "image/jpeg";
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (RuntimeException e) {
            log.error("❌ Error al obtener evidencia: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Eliminar una denuncia
     * DELETE /api/denuncias/{id}
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDenuncia(@PathVariable Long id) {
        try {
            denunciaService.eliminarDenuncia(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Denuncia eliminada correctamente");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al eliminar denuncia: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Obtener comentarios internos de una denuncia
     * GET /api/denuncias/{id}/comentarios
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<?> obtenerComentarios(@PathVariable Long id) {
        try {
            List<ComentarioInternoResponse> comentarios = adminService.obtenerComentariosInternos(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", comentarios.size());
            response.put("comentarios", comentarios);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al obtener comentarios: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Obtener historial de acciones
     * GET /api/denuncias/{id}/historial
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @GetMapping("/{id}/historial")
    public ResponseEntity<?> obtenerHistorial(@PathVariable Long id) {
        try {
            List<HistorialAccionResponse> historial = adminService.obtenerHistorialAcciones(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", historial.size());
            response.put("historial", historial);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al obtener historial: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Listar denuncias que YO he revisado
     * GET /api/denuncias/mis-revisadas?email=funcionario@municipalidad.cl
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @GetMapping("/mis-revisadas")
    public ResponseEntity<?> listarDenunciasRevisadas(@RequestParam String email) {
        try {
            List<DenunciaResponse> denuncias = adminService.listarDenunciasRevisadas(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("email", email);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al obtener denuncias revisadas: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Agregar comentario interno
     * POST /api/denuncias/{id}/comentarios
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<?> agregarComentario(
            @PathVariable Long id,
            @RequestBody ComentarioInternoRequest request) {

        try {
            log.info("💬 Agregando comentario interno a denuncia ID: {}", id);

            ComentarioInternoResponse comentario = adminService.agregarComentarioInterno(id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Comentario agregado exitosamente");
            response.put("comentario", comentario);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al agregar comentario: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Eliminar comentario interno
     * DELETE /api/denuncias/comentarios/{comentarioId}
     */
    @PreAuthorize("hasRole('FUNCIONARIO')")
    @DeleteMapping("/comentarios/{comentarioId}")
    public ResponseEntity<?> eliminarComentario(
            @PathVariable Long comentarioId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            log.info("🗑️ Eliminando comentario interno ID: {}", comentarioId);

            String emailFuncionario = roleValidator.obtenerEmailDesdeToken(authHeader);
            adminService.eliminarComentarioInterno(comentarioId, emailFuncionario);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Comentario eliminado exitosamente");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al eliminar comentario: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Cambiar estado de una denuncia
     * PUT /api/denuncias/{id}/estado
     */
    @PreAuthorize("hasAnyRole('FUNCIONARIO')")
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoDenunciaRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        try {
            log.info("🔄 Cambiando estado de denuncia ID: {}", id);

            String emailRevisor = roleValidator.obtenerEmailDesdeToken(authHeader);
            DenunciaResponse denuncia = adminService.cambiarEstadoDenuncia(id, request, emailRevisor);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado exitosamente");
            response.put("denuncia", denuncia);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al cambiar estado: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

}