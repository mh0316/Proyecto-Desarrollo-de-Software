package com.example.appmunicipal.DTO;

import com.example.appmunicipal.domain.Evidencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaResponse {

    private Long id;
    private Long denunciaId;
    private String tipo;
    private String nombreArchivo;
    private String url;
    private String mimeType;
    private Long tamanoBytes;
    private LocalDateTime fechaSubida;

    // Constructor desde entidad Evidencia
    public EvidenciaResponse(Evidencia evidencia) {
        this.id = evidencia.getId();
        this.denunciaId = evidencia.getDenuncia().getId();
        this.tipo = evidencia.getTipo().name();
        this.nombreArchivo = evidencia.getNombreArchivo();
        // Construir URL para acceder a la evidencia
        this.url = "/api/denuncias/evidencia/" + evidencia.getNombreArchivo();
        this.mimeType = evidencia.getMimeType();
        this.tamanoBytes = evidencia.getTamanoBytes();
        this.fechaSubida = evidencia.getFechaSubida();
    }
}
