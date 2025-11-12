package com.example.appmunicipal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioInternoRequest {

    private String emailUsuario;        // Email del administrador/revisor
    private String comentario;          // Contenido del comentario
}