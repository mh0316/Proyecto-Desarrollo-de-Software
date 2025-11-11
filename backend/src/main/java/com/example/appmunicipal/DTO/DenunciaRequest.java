package com.example.appmunicipal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DenunciaRequest {

    // Usuario
    private String email;           // Email del usuario que crea la denuncia (obligatorio)

    // Datos de la denuncia
    private Long categoriaId;       // ID de la categoría (obligatorio)
    private String descripcion;     // Descripción de la denuncia (obligatorio)
    private String patente;         // Patente del vehículo (opcional)
    private Double latitud;         // Coordenada latitud (obligatorio)
    private Double longitud;        // Coordenada longitud (obligatorio)
    private String direccion;       // Dirección legible (opcional)
    private String sector;          // Sector de Temuco (opcional)
    private String comuna;          // Comuna (opcional, ej: Temuco)
}