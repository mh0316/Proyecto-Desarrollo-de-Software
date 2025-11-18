package com.example.appmunicipal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambiarEstadoDenunciaRequest {

    private String emailRevisor;        // Email del revisor
    private String nuevoEstado;         // PENDIENTE, EN_REVISION, VALIDADA, RECHAZADA, CERRADA
    private String comentario;          // Opcional
}