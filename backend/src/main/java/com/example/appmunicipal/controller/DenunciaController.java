package com.example.appmunicipal.controller;

import com.example.appmunicipal.DTO.DenunciaRequest;
import com.example.appmunicipal.DTO.DenunciaResponse;
import com.example.appmunicipal.service.DenunciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/denuncias")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DenunciaController {

    private final DenunciaService denunciaService;

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
    @GetMapping
    public ResponseEntity<?> listarDenuncias() {
        try {
            List<DenunciaResponse> denuncias = denunciaService.listarTodasLasDenuncias();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", denuncias.size());
            response.put("denuncias", denuncias);

            return ResponseEntity.ok(response);

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
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
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
}