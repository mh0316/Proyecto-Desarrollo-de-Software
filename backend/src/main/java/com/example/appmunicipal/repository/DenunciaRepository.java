package com.example.appmunicipal.repository;

import com.example.appmunicipal.domain.Denuncia;
import com.example.appmunicipal.domain.Denuncia.EstadoDenuncia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

    // ========================================
    // MÉTODOS PAGINADOS (NUEVOS)
    // ========================================

    // Paginación básica ordenada por fecha
    Page<Denuncia> findAllByOrderByFechaDenunciaDesc(Pageable pageable);

    // Paginación con filtro por estado
    Page<Denuncia> findByEstadoOrderByFechaDenunciaDesc(EstadoDenuncia estado, Pageable pageable);

    // Paginación con filtro por categoría
    Page<Denuncia> findByCategoriaIdOrderByFechaDenunciaDesc(Long categoriaId, Pageable pageable);

    // Paginación con filtro por comuna
    Page<Denuncia> findByComunaOrderByFechaDenunciaDesc(String comuna, Pageable pageable);

    // Paginación con filtro por patente
    Page<Denuncia> findByPatenteContainingIgnoreCaseOrderByFechaDenunciaDesc(String patente, Pageable pageable);

    // ========================================
    // MÉTODOS EXISTENTES (SIN PAGINACIÓN)
    // ========================================

    // Búsquedas básicas
    List<Denuncia> findByUsuarioId(Long usuarioId);

    List<Denuncia> findByEstado(EstadoDenuncia estado);

    List<Denuncia> findByCategoriaId(Long categoriaId);

    List<Denuncia> findByPatente(String patente);

    List<Denuncia> findByRevisorId(Long revisorId);

    // Ordenar por fecha descendente (más recientes primero)
    List<Denuncia> findAllByOrderByFechaDenunciaDesc();

    List<Denuncia> findByUsuarioIdOrderByFechaDenunciaDesc(Long usuarioId);

    // Filtros por fecha
    List<Denuncia> findByFechaDenunciaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Denuncias por sector
    List<Denuncia> findBySector(String sector);

    // Denuncias por comuna
    List<Denuncia> findByComuna(String comuna);

    // Contar denuncias por usuario
    Long countByUsuarioId(Long usuarioId);

    // Contar denuncias por estado
    Long countByEstado(EstadoDenuncia estado);
}