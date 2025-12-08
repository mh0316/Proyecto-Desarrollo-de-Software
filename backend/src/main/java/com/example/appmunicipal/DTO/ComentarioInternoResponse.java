package com.example.appmunicipal.DTO;

import com.example.appmunicipal.domain.ComentarioInterno;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioInternoResponse {

    private Long id;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
    private String comentario;
    private LocalDateTime fechaComentario;

    public ComentarioInternoResponse(ComentarioInterno comentario) {
        this.id = comentario.getId();
        this.nombreUsuario = comentario.getUsuario().getNombre();
        this.apellidoUsuario = comentario.getUsuario().getApellido();
        this.emailUsuario = comentario.getUsuario().getEmail();
        this.comentario = comentario.getComentario();
        this.fechaComentario = comentario.getFechaComentario();
    }
}