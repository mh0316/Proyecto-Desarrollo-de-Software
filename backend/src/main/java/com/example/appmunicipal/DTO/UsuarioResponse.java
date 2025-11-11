package com.example.appmunicipal.DTO;

import com.example.appmunicipal.domain.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long id;
    private String username;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private LocalDateTime fechaRegistro;

    // Constructor desde entidad Usuario
    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol().getNombre();
        this.fechaRegistro = usuario.getFechaRegistro();
    }
}