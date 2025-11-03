package com.example.appmunicipal.repository;

import com.example.appmunicipal.domain.Denuncia;
import com.example.appmunicipal.domain.Denuncia.EstadoDenuncia;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Denuncias por sector (E8)
    List<Denuncia> findBySector(String sector);

    // Denuncias por comuna (E9)
    List<Denuncia> findByComuna(String comuna);

}