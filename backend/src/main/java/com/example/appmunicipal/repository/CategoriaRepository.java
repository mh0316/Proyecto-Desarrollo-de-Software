package com.example.appmunicipal.repository;

import com.example.appmunicipal.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombre(String nombre);

    Optional<Categoria> findByCodigo(String codigo);

    List<Categoria> findByActivaTrue();

    boolean existsByNombre(String nombre);

    boolean existsByCodigo(String codigo);
}