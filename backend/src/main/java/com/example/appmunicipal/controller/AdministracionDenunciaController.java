package com.example.appmunicipal.controller;

import com.example.appmunicipal.DTO.*;
import com.example.appmunicipal.service.AdministracionDenunciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/funcionario/denuncias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdministracionDenunciaController {

    private final AdministracionDenunciaService adminService;

    /**
     * Listar TODAS las denuncias (para funcionarios)
     * GET /api/funcionario/denuncias?email=funcionario@municipalidad.cl
     */
    @GetMapping
    public ResponseEntity<?> listarTodasDenuncias(@RequestParam String email) {
        try {
            List<DenunciaResponse> denuncias = adminService.listarTodasParaFuncionario(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al listar denuncias: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }

    /**
     * Validar o Rechazar una denuncia
     * POST /api/funcionario/denuncias/{id}/validar
     */
    @PostMapping("/{id}/validar")
    public ResponseEntity<?> validarORechazarDenuncia(
            @PathVariable Long id,
            @RequestBody ValidarDenunciaRequest request) {

        try {
            log.info("🔍 Validando/Rechazando denuncia ID: {}", id);

            DenunciaResponse denuncia = adminService.validarORechazarDenuncia(id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Denuncia " + request.getAccion().toLowerCase() + "da exitosamente");
            response.put("denuncia", denuncia);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error al validar/rechazar denuncia: {}", e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Cambiar estado de una denuncia
     * PUT /api/funcionario/denuncias/{id}/estado
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoDenunciaRequest request) {

        try {
            log.info("🔄 Cambiando estado de denuncia ID: {}", id);

            DenunciaResponse denuncia = adminService.cambiarEstadoDenuncia(id, request);

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

    /**
     * Agregar comentario interno
     * POST /api/funcionario/denuncias/{id}/comentarios
     */
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
     * Obtener comentarios internos de una denuncia
     * GET /api/funcionario/denuncias/{id}/comentarios
     */
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
     * GET /api/funcionario/denuncias/{id}/historial
     */
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
     * GET /api/funcionario/denuncias/mis-revisadas?email=funcionario@municipalidad.cl
     */
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
}