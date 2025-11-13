package com.example.appmunicipal.repository;

import com.example.appmunicipal.domain.Notificacion;
import com.example.appmunicipal.domain.Notificacion.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuarioId(Long usuarioId);

    List<Notificacion> findByUsuarioIdAndLeidaFalse(Long usuarioId);

    List<Notificacion> findByDenunciaId(Long denunciaId);

    List<Notificacion> findByTipo(TipoNotificacion tipo);

    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    Long countByUsuarioIdAndLeidaFalse(Long usuarioId);
}