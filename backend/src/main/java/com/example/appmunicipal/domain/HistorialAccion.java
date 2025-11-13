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
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAccion tipoAccion;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "fecha_accion", nullable = false, updatable = false)
    private LocalDateTime fechaAccion;

    @PrePersist
    protected void onCreate() {
        fechaAccion = LocalDateTime.now();
    }

    public enum TipoAccion {
        CREACION,           // Cuando se crea la denuncia
        VALIDACION,         // Cuando un funcionario valida la denuncia
        RECHAZO,            // Cuando un funcionario rechaza la denuncia
        CAMBIO_ESTADO,      // Cuando se cambia el estado
        COMENTARIO,         // Cuando se agrega un comentario interno
        EVIDENCIA           // Cuando se agrega evidencia
    }
}