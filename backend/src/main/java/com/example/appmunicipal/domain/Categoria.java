package com.example.appmunicipal.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @Column(length = 50)
    private String codigo;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(length = 7)
    private String colorHex;

    @OneToMany(mappedBy = "categoria")
    private List<Denuncia> denuncias = new ArrayList<>();
}