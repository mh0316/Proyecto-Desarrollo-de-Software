package com.example.appmunicipal.repository;


import com.example.appmunicipal.domain.HistorialAccion;
import com.example.appmunicipal.domain.HistorialAccion.TipoAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {

    List<HistorialAccion> findByDenunciaId(Long denunciaId);

    List<HistorialAccion> findByUsuarioId(Long usuarioId);

    List<HistorialAccion> findByTipoAccion(TipoAccion tipoAccion);

    List<HistorialAccion> findByDenunciaIdOrderByFechaAccionDesc(Long denunciaId);

}