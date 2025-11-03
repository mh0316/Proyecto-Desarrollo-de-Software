package com.example.appmunicipal.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_acciones")
@Data
public class HistorialAccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "denuncia_id", nullable = false)
    private Denuncia denuncia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAccion tipoAccion;

    @Column(length = 50)
    private String estadoAnterior;

    @Column(length = 50)
    private String estadoNuevo;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "fecha_accion", nullable = false, updatable = false)
    private LocalDateTime fechaAccion;

    @Column(length = 50)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        fechaAccion = LocalDateTime.now();
    }

    public enum TipoAccion {
        CREAR_DENUNCIA,
        ASIGNAR_REVISOR,
        CAMBIAR_ESTADO,
        VALIDAR,
        RECHAZAR,
        AGREGAR_COMENTARIO,
        EDITAR_DENUNCIA,
        ELIMINAR_DENUNCIA,
        AGREGAR_EVIDENCIA
    }
}