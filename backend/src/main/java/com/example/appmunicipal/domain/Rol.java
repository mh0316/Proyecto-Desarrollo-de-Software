package com.example.appmunicipal.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 200)
    private String descripcion;

    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios = new ArrayList<>();

    // Constantes para nombres de roles
    public static final String CIUDADANO = "CIUDADANO";
    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String REVISOR = "REVISOR";
    public static final String SUPERVISOR = "SUPERVISOR";
}