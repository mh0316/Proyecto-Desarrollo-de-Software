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
    private String telefono;
    private String rut;
    private RolSimpleDto rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimaConexion;

    // Constructor desde entidad Usuario
    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.telefono = usuario.getTelefono();
        this.rut = usuario.getRut();
        this.rol = new RolSimpleDto(usuario.getRol());
        this.activo = usuario.getActivo();
        this.fechaRegistro = usuario.getFechaRegistro();
        this.ultimaConexion = usuario.getUltimaConexion();
    }

    // DTO interno para Rol (sin lista de usuarios)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolSimpleDto {
        private Long id;
        private String nombre;
        private String descripcion;

        public RolSimpleDto(com.example.appmunicipal.domain.Rol rol) {
            this.id = rol.getId();
            this.nombre = rol.getNombre();
            this.descripcion = rol.getDescripcion();
        }
    }
}