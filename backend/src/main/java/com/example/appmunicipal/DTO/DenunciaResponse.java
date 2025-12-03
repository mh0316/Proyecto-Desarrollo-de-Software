package com.example.appmunicipal.DTO;

import com.example.appmunicipal.domain.Denuncia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaResponse {

    private Long id;
    private String emailUsuario; // Solo email del usuario (seguro)
    private String nombreUsuario; // Solo nombre (para mostrar)
    private CategoriaSimpleDto categoria;
    private String descripcion;
    private String patente;
    private Double latitud;
    private Double longitud;
    private String direccion;
    private String sector;
    private String comuna;
    private String estado;
    private LocalDateTime fechaDenuncia;
    private LocalDateTime fechaValidacion;
    private String motivoRechazo;
    private Integer cantidadEvidencias;
    private List<String> evidenciasUrls;

    // Constructor desde entidad Denuncia
    public DenunciaResponse(Denuncia denuncia) {
        this.id = denuncia.getId();

        // Solo email y nombre del usuario (seguro, sin exponer más datos)
        this.emailUsuario = denuncia.getUsuario().getEmail();
        this.nombreUsuario = denuncia.getUsuario().getNombre();

        // Categoría
        this.categoria = new CategoriaSimpleDto(
                denuncia.getCategoria().getId(),
                denuncia.getCategoria().getNombre(),
                denuncia.getCategoria().getCodigo(),
                denuncia.getCategoria().getColorHex());

        // Datos de la denuncia
        this.descripcion = denuncia.getDescripcion();
        this.patente = denuncia.getPatente();
        this.latitud = denuncia.getLatitud();
        this.longitud = denuncia.getLongitud();
        this.direccion = denuncia.getDireccion();
        this.sector = denuncia.getSector();
        this.comuna = denuncia.getComuna();
        this.estado = denuncia.getEstado().name();
        this.fechaDenuncia = denuncia.getFechaDenuncia();
        this.fechaValidacion = denuncia.getFechaValidacion();
        this.motivoRechazo = denuncia.getMotivoRechazo();
        this.cantidadEvidencias = denuncia.getEvidencias() != null ? denuncia.getEvidencias().size() : 0;

        // Generar URLs de evidencias
        this.evidenciasUrls = denuncia.getEvidencias() != null
                ? denuncia.getEvidencias().stream()
                        .map(evidencia -> "/api/denuncias/evidencia/" + evidencia.getNombreArchivo())
                        .collect(Collectors.toList())
                : List.of();
    }

    // DTO interno para Categoría
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaSimpleDto {
        private Long id;
        private String nombre;
        private String codigo;
        private String colorHex;
    }
}