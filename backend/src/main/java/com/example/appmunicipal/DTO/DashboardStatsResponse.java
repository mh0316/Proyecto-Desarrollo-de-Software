package com.example.appmunicipal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalDenuncias;
    private Map<String, Long> denunciasPorMes;
    private Map<String, Long> denunciasPorCategoria;
    private Map<String, Long> denunciasPorEstado;
    private Double tasaValidacion;
    private Double tasaRechazo;
    private Double tiempoPromedioValidacion;
    private Map<Integer, Long> denunciasPorHorario;
    private Map<String, Long> denunciasPorComuna;
    private Map<String, Long> denunciasPorSector;
    private Map<String, Long> topUsuarios;
    private Map<String, Long> reincidenciaPatentes;
}
