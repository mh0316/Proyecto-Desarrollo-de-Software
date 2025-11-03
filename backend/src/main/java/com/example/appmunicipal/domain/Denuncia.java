package com.example.appmunicipal.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "denuncias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(length = 20)
    private String patente;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String sector;

    @Column(length = 100)
    private String comuna;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoDenuncia estado = EstadoDenuncia.PENDIENTE;

    @Column(name = "fecha_denuncia", nullable = false, updatable = false)
    private LocalDateTime fechaDenuncia;

    @Column(name = "fecha_validacion")
    private LocalDateTime fechaValidacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revisor_id")
    private Usuario revisor;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    //@OneToMany(mappedBy = "denuncia", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Evidencia> evidencias = new ArrayList<>();

    //@OneToMany(mappedBy = "denuncia", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<ComentarioInterno> comentariosInternos = new ArrayList<>();

    //@OneToMany(mappedBy = "denuncia", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<HistorialAccion> historial = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaDenuncia = LocalDateTime.now();
    }

    public enum EstadoDenuncia {
        PENDIENTE,
        EN_REVISION,
        VALIDADA,
        RECHAZADA,
        CERRADA
    }
}