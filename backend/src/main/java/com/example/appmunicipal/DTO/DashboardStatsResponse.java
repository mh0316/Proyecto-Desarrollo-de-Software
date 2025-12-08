package com.example.appmunicipal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Map<String, Long> denunciasPorMes;
    private Map<String, Long> denunciasPorCategoria;
    private Double tasaValidacion;
    private Double tasaRechazo;
    private Double tiempoPromedioValidacion; // En horas
    private Map<Integer, Long> denunciasPorHorario;
    private Map<String, Long> denunciasPorComuna; // E9
    private Map<String, Long> denunciasPorSector; // E8 (Temuco priority)

    // T4: Top usuarios denunciantes (Email/Nombre -> Cantidad)
    private Map<String, Long> topUsuarios;

    // E10: Reincidencia por patente (Patente -> Cantidad)
    private Map<String, Long> reincidenciaPatentes;
}
