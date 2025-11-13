package com.example.appmunicipal.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidencias")
@Data
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "denuncia_id", nullable = false)
    private Denuncia denuncia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEvidencia tipo;

    @Column(nullable = false, length = 500)
    private String rutaArchivo;

    @Column(length = 200)
    private String nombreArchivo;

    @Column(length = 50)
    private String mimeType;

    @Column
    private Long tamanoBytes;

    @Column(name = "fecha_subida", nullable = false, updatable = false)
    private LocalDateTime fechaSubida;

    @PrePersist
    protected void onCreate() {
        fechaSubida = LocalDateTime.now();
    }

    public enum TipoEvidencia {
        FOTO,
        VIDEO
    }
}