package com.example.appmunicipal.DTO;

import com.example.appmunicipal.domain.HistorialAccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialAccionResponse {

    private Long id;
    private String nombreUsuario;
    private String emailUsuario;
    private String tipoAccion;
    private String descripcion;
    private LocalDateTime fechaAccion;

    public HistorialAccionResponse(HistorialAccion historial) {
        this.id = historial.getId();
        this.nombreUsuario = historial.getUsuario().getNombre();
        this.emailUsuario = historial.getUsuario().getEmail();
        this.tipoAccion = historial.getTipoAccion().name();
        this.descripcion = historial.getDescripcion();
        this.fechaAccion = historial.getFechaAccion();
    }
}