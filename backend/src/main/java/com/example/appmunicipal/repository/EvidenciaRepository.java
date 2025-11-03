package com.example.appmunicipal.repository;

import com.example.appmunicipal.domain.Evidencia;
import com.example.appmunicipal.domain.Evidencia.TipoEvidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {

    List<Evidencia> findByDenunciaId(Long denunciaId);

    List<Evidencia> findByTipo(TipoEvidencia tipo);

    List<Evidencia> findByDenunciaIdAndTipo(Long denunciaId, TipoEvidencia tipo);

    Long countByDenunciaId(Long denunciaId);
}