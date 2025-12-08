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

    // Paginación - Listar todas las denuncias ordenadas por fecha descendente
    Page<Denuncia> findAllByOrderByFechaDenunciaDesc(Pageable pageable);

    // ==========================================
    // ESTADÍSTICAS OPTIMIZADAS (JPQL)
    // ==========================================

    // Cantidad de denuncias por Mes (últimos 12 meses)
    // Nota: FUNCTION('MONTH', ...) es estándar JPA. Para H2/MySQL funciona bien.
    @Query("SELECT FUNCTION('MONTH', d.fechaDenuncia) as mes, COUNT(d) as cantidad " +
            "FROM Denuncia d " +
            "WHERE d.fechaDenuncia >= :fechaInicio " +
            "GROUP BY FUNCTION('MONTH', d.fechaDenuncia)")
    List<Object[]> countDenunciasByMes(@Param("fechaInicio") LocalDateTime fechaInicio);

    // Cantidad de denuncias por Estado
    @Query("SELECT d.estado, COUNT(d) FROM Denuncia d GROUP BY d.estado")
    List<Object[]> countDenunciasByEstadoGrouped();

    // Cantidad de denuncias por Comuna
    @Query("SELECT UPPER(d.comuna), COUNT(d) FROM Denuncia d WHERE d.comuna IS NOT NULL GROUP BY UPPER(d.comuna)")
    List<Object[]> countDenunciasByComuna();

    // Cantidad de denuncias por Horario (Hora del día 0-23)
    @Query("SELECT FUNCTION('HOUR', d.fechaDenuncia), COUNT(d) FROM Denuncia d GROUP BY FUNCTION('HOUR', d.fechaDenuncia)")
    List<Object[]> countDenunciasByHora();

    // Cantidad de denuncias por Categoría
    @Query("SELECT c.nombre, COUNT(d) FROM Denuncia d JOIN d.categoria c GROUP BY c.id, c.nombre ORDER BY COUNT(d) DESC")
    List<Object[]> countDenunciasByCategoria();

    // Tiempo promedio de validación (en horas)
    // Solo para denuncias que tienen fechaValidacion
    @Query("SELECT d.fechaDenuncia, d.fechaValidacion FROM Denuncia d WHERE d.fechaValidacion IS NOT NULL")
    List<Object[]> findFechasParaPromedioValidacion();

    // Cantidad de denuncias por Sector (Solo Temuco)

    @Query("SELECT UPPER(d.sector), COUNT(d) FROM Denuncia d " +
            "WHERE d.sector IS NOT NULL AND UPPER(d.comuna) LIKE '%TEMUCO%' " +
            "GROUP BY UPPER(d.sector)")
    List<Object[]> countDenunciasBySectorTemuco();

    // Top usuarios denunciantes
    @Query("SELECT CONCAT(u.nombre, ' ', u.apellido, ' (', u.email, ')'), COUNT(d) " +
            "FROM Denuncia d JOIN d.usuario u " +
            "GROUP BY u.id, u.nombre, u.apellido, u.email " +
            "ORDER BY COUNT(d) DESC")
    List<Object[]> countDenunciasByUsuarioTop10(Pageable pageable);

    // Reincidencia por patente (solo patentes con más de 1 denuncia)
    @Query("SELECT UPPER(TRIM(d.patente)), COUNT(d) " +
            "FROM Denuncia d " +
            "WHERE d.patente IS NOT NULL AND d.patente != '' " +
            "GROUP BY UPPER(TRIM(d.patente)) " +
            "HAVING COUNT(d) > 1 " +
            "ORDER BY COUNT(d) DESC")
    List<Object[]> countReincidenciaByPatente(Pageable pageable);
}