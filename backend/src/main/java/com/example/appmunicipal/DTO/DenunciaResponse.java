package com.example.appmunicipal.DTO;

import com.example.appmunicipal.domain.Denuncia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaResponse {

    private Long id;
    private UsuarioSimpleDto usuario;
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
    private UsuarioSimpleDto revisor;
    private String motivoRechazo;
    private Integer cantidadEvidencias;

    // Constructor desde entidad
    public DenunciaResponse(Denuncia denuncia) {
        this.id = denuncia.getId();
        this.usuario = new UsuarioSimpleDto(
                denuncia.getUsuario().getId(),
                denuncia.getUsuario().getUsername(),
                denuncia.getUsuario().getNombre(),
                denuncia.getUsuario().getEmail()
        );
        this.categoria = new CategoriaSimpleDto(
                denuncia.getCategoria().getId(),
                denuncia.getCategoria().getNombre(),
                denuncia.getCategoria().getCodigo(),
                denuncia.getCategoria().getColorHex()
        );
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

        if (denuncia.getRevisor() != null) {
            this.revisor = new UsuarioSimpleDto(
                    denuncia.getRevisor().getId(),
                    denuncia.getRevisor().getUsername(),
                    denuncia.getRevisor().getNombre(),
                    denuncia.getRevisor().getEmail()
            );
        }

        this.motivoRechazo = denuncia.getMotivoRechazo();
        this.cantidadEvidencias = denuncia.getEvidencias() != null ? denuncia.getEvidencias().size() : 0;
    }

    // DTOs internos
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioSimpleDto {
        private Long id;
        private String username;
        private String nombre;
        private String email;
    }

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