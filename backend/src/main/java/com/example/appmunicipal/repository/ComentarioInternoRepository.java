package com.example.appmunicipal.repository;

import com.example.appmunicipal.domain.ComentarioInterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioInternoRepository extends JpaRepository<ComentarioInterno, Long> {

    List<ComentarioInterno> findByDenunciaId(Long denunciaId);

    List<ComentarioInterno> findByUsuarioId(Long usuarioId);

    List<ComentarioInterno> findByDenunciaIdOrderByFechaComentarioDesc(Long denunciaId);
}