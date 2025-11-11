package com.example.appmunicipal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequest {

    private String username;      // Opcional - se genera automáticamente si no viene
    private String password;      // Obligatorio
    private String nombre;        // Obligatorio
    private String apellido;      // Obligatorio
    private String email;         // Obligatorio
    private String telefono;      // Opcional
    private String rut;           // Obligatorio ✅

    // rolId ya NO se usa, siempre será CIUDADANO
}