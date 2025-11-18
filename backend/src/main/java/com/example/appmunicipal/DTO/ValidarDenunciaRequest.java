package com.example.appmunicipal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarDenunciaRequest {

    private String emailRevisor;        // Email del revisor que valida/rechaza
    private String accion;              // "VALIDAR" o "RECHAZAR"
    private String motivo;              // Obligatorio si es RECHAZAR
}