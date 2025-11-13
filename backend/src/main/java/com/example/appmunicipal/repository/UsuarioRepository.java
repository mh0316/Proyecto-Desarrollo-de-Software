package com.example.appmunicipal.repository;

import com.example.appmunicipal.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByRut(String rut);

    List<Usuario> findByRolId(Long rolId);

    List<Usuario> findByActivoTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);  // ✅ Para validar RUT duplicado
}