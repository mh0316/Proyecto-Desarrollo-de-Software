package com.example.appmunicipal.DTO;

import com.example.appmunicipal.domain.Denuncia;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO ligero para listar denuncias
 * Solo incluye campos esenciales para mejorar el rendimiento
 */
@Data
@NoArgsConstructor
public class DenunciaLightResponse {
    private Long id;
    private String descripcionCorta; // Truncada a 100 caracteres
    private String estado;
    private LocalDateTime fechaDenuncia;
    private String patente;
    private String comuna;
    private String sector;
    private String direccion;
    private Integer cantidadEvidencias;
    private String categoriaNombre;
    private String usuarioNombre;

    public DenunciaLightResponse(Denuncia denuncia) {
        this.id = denuncia.getId();

        // Truncar descripción a 100 caracteres
        if (denuncia.getDescripcion() != null) {
            this.descripcionCorta = denuncia.getDescripcion().length() > 100
                    ? denuncia.getDescripcion().substring(0, 100) + "..."
                    : denuncia.getDescripcion();
        }

        this.estado = denuncia.getEstado() != null ? denuncia.getEstado().name() : null;
        this.fechaDenuncia = denuncia.getFechaDenuncia();
        this.patente = denuncia.getPatente();
        this.comuna = denuncia.getComuna();
        this.sector = denuncia.getSector();
        this.direccion = denuncia.getDireccion();

        // Contar evidencias (lazy loading safe)
        this.cantidadEvidencias = denuncia.getEvidencias() != null
                ? denuncia.getEvidencias().size()
                : 0;

        // Información de categoría
        this.categoriaNombre = denuncia.getCategoria() != null
                ? denuncia.getCategoria().getNombre()
                : null;

        // Información básica del usuario
        if (denuncia.getUsuario() != null) {
            this.usuarioNombre = denuncia.getUsuario().getNombre() + " " +
                    denuncia.getUsuario().getApellido();
        }
    }
}
