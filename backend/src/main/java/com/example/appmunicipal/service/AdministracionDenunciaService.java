package com.example.appmunicipal.service;

import com.example.appmunicipal.domain.*;
import com.example.appmunicipal.DTO.*;
import com.example.appmunicipal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministracionDenunciaService {

    private final DenunciaRepository denunciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComentarioInternoRepository comentarioInternoRepository;
    private final HistorialAccionRepository historialAccionRepository;

    /**
     * Validar o Rechazar una denuncia
     * Solo FUNCIONARIO puede ejecutar esta acción
     */
    @Transactional
    public DenunciaResponse validarORechazarDenuncia(Long denunciaId, ValidarDenunciaRequest request) {
        log.info("🔍 Validando/Rechazando denuncia ID: {} por {}", denunciaId, request.getEmailRevisor());

        // Validar campos obligatorios
        if (request.getEmailRevisor() == null || request.getEmailRevisor().trim().isEmpty()) {
            throw new RuntimeException("El email del funcionario es obligatorio");
        }

        if (request.getAccion() == null || request.getAccion().trim().isEmpty()) {
            throw new RuntimeException("La acción es obligatoria (VALIDAR o RECHAZAR)");
        }

        // Obtener funcionario
        Usuario funcionario = usuarioRepository.findByEmail(request.getEmailRevisor())
                .orElseThrow(() -> new RuntimeException(
                        "Funcionario no encontrado con email: " + request.getEmailRevisor()));

        // Verificar que el usuario sea FUNCIONARIO
        if (!funcionario.getRol().getNombre().equals(Rol.FUNCIONARIO)) {
            throw new RuntimeException("Solo usuarios con rol FUNCIONARIO pueden validar/rechazar denuncias");
        }

        // Obtener denuncia
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + denunciaId));

        // Verificar que la denuncia esté en estado válido
        if (denuncia.getEstado() == Denuncia.EstadoDenuncia.VALIDADA ||
                denuncia.getEstado() == Denuncia.EstadoDenuncia.RECHAZADA) {
            throw new RuntimeException("La denuncia ya ha sido " + denuncia.getEstado().name().toLowerCase());
        }

        String accion = request.getAccion().toUpperCase();

        if (accion.equals("VALIDAR")) {
            // Validar denuncia
            denuncia.setEstado(Denuncia.EstadoDenuncia.VALIDADA);
            denuncia.setRevisor(funcionario);
            denuncia.setFechaValidacion(LocalDateTime.now());
            denuncia.setMotivoRechazo(null);

            log.info("✅ Denuncia {} VALIDADA por {}", denunciaId, funcionario.getUsername());

            // Registrar en historial
            registrarAccion(denuncia, funcionario, HistorialAccion.TipoAccion.VALIDACION,
                    "Denuncia validada por funcionario " + funcionario.getNombre() + " " + funcionario.getApellido());

        } else if (accion.equals("RECHAZAR")) {
            // Validar que venga el motivo
            if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
                throw new RuntimeException("El motivo de rechazo es obligatorio");
            }

            // Rechazar denuncia
            denuncia.setEstado(Denuncia.EstadoDenuncia.RECHAZADA);
            denuncia.setRevisor(funcionario);
            denuncia.setFechaValidacion(LocalDateTime.now());
            denuncia.setMotivoRechazo(request.getMotivo());

            log.warn("❌ Denuncia {} RECHAZADA por {}. Motivo: {}",
                    denunciaId, funcionario.getUsername(), request.getMotivo());

            // Registrar en historial
            registrarAccion(denuncia, funcionario, HistorialAccion.TipoAccion.RECHAZO,
                    "Denuncia rechazada. Motivo: " + request.getMotivo());

        } else {
            throw new RuntimeException("Acción inválida. Use 'VALIDAR' o 'RECHAZAR'");
        }

        // Guardar cambios
        Denuncia denunciaActualizada = denunciaRepository.save(denuncia);

        return new DenunciaResponse(denunciaActualizada);
    }

    /**
     * Cambiar estado de una denuncia
     */
    @Transactional
    public DenunciaResponse cambiarEstadoDenuncia(Long denunciaId, CambiarEstadoDenunciaRequest request,
            String emailFuncionario) {
        log.info("🔄 Cambiando estado de denuncia ID: {} por {}", denunciaId, emailFuncionario);

        if (request.getEstado() == null || request.getEstado().trim().isEmpty()) {
            throw new RuntimeException("El nuevo estado es obligatorio");
        }

        // Obtener funcionario
        Usuario funcionario = usuarioRepository.findByEmail(emailFuncionario)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado"));

        // Verificar rol
        if (!funcionario.getRol().getNombre().equals(Rol.FUNCIONARIO)) {
            throw new RuntimeException("Solo usuarios con rol FUNCIONARIO pueden cambiar el estado");
        }

        // Obtener denuncia
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + denunciaId));

        // Convertir string a enum
        Denuncia.EstadoDenuncia nuevoEstado;
        try {
            nuevoEstado = Denuncia.EstadoDenuncia.valueOf(request.getEstado().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado inválido: " + request.getEstado() +
                    ". Estados válidos: PENDIENTE, EN_REVISION, VALIDADA, RECHAZADA, CERRADA");
        }

        Denuncia.EstadoDenuncia estadoAnterior = denuncia.getEstado();

        // Cambiar estado
        denuncia.setEstado(nuevoEstado);

        // Si pasa a EN_REVISION y no tiene revisor, asignarlo
        if (nuevoEstado == Denuncia.EstadoDenuncia.EN_REVISION && denuncia.getRevisor() == null) {
            denuncia.setRevisor(funcionario);
        }

        log.info("✅ Estado cambiado de {} a {} por {}",
                estadoAnterior, nuevoEstado, funcionario.getUsername());

        // Registrar en historial
        String descripcion = String.format("Estado cambiado de %s a %s por funcionario %s %s",
                estadoAnterior.name(), nuevoEstado.name(), funcionario.getNombre(), funcionario.getApellido());
        if (request.getComentario() != null && !request.getComentario().trim().isEmpty()) {
            descripcion += ". Comentario: " + request.getComentario();
        }

        registrarAccion(denuncia, funcionario, HistorialAccion.TipoAccion.CAMBIO_ESTADO, descripcion);

        // Guardar
        Denuncia denunciaActualizada = denunciaRepository.save(denuncia);

        return new DenunciaResponse(denunciaActualizada);
    }

    /**
     * Agregar comentario interno a una denuncia
     */
    @Transactional
    public ComentarioInternoResponse agregarComentarioInterno(Long denunciaId, ComentarioInternoRequest request) {
        log.info("💬 Agregando comentario interno a denuncia ID: {} por {}",
                denunciaId, request.getEmailUsuario());

        // Validaciones
        if (request.getEmailUsuario() == null || request.getEmailUsuario().trim().isEmpty()) {
            throw new RuntimeException("El email del usuario es obligatorio");
        }

        if (request.getComentario() == null || request.getComentario().trim().isEmpty()) {
            throw new RuntimeException("El comentario no puede estar vacío");
        }

        // Obtener usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmailUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar rol (solo FUNCIONARIO puede agregar comentarios internos)
        if (!usuario.getRol().getNombre().equals(Rol.FUNCIONARIO)) {
            throw new RuntimeException("Solo usuarios con rol FUNCIONARIO pueden agregar comentarios internos");
        }

        // Obtener denuncia
        Denuncia denuncia = denunciaRepository.findById(denunciaId)
                .orElseThrow(() -> new RuntimeException("Denuncia no encontrada con ID: " + denunciaId));

        // Crear comentario
        ComentarioInterno comentario = new ComentarioInterno();
        comentario.setDenuncia(denuncia);
        comentario.setUsuario(usuario);
        comentario.setComentario(request.getComentario());
        comentario.setFechaComentario(LocalDateTime.now());

        // Guardar
        ComentarioInterno comentarioGuardado = comentarioInternoRepository.save(comentario);

        log.info("✅ Comentario interno agregado por {}", usuario.getUsername());

        // Registrar en historial
        registrarAccion(denuncia, usuario, HistorialAccion.TipoAccion.COMENTARIO,
                "Comentario interno agregado por funcionario " + usuario.getNombre() + " " + usuario.getApellido());

        return new ComentarioInternoResponse(comentarioGuardado);
    }

    /**
     * Eliminar comentario interno
     */
    @Transactional
    public void eliminarComentarioInterno(Long comentarioId, String emailFuncionario) {
        log.info("🗑️ Eliminando comentario interno ID: {} por {}", comentarioId, emailFuncionario);

        // Obtener comentario
        ComentarioInterno comentario = comentarioInternoRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado con ID: " + comentarioId));

        // Obtener funcionario
        Usuario funcionario = usuarioRepository.findByEmail(emailFuncionario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que es funcionario
        if (!funcionario.getRol().getNombre().equals(Rol.FUNCIONARIO)) {
            throw new RuntimeException("Solo usuarios con rol FUNCIONARIO pueden eliminar comentarios internos");
        }

        Denuncia denuncia = comentario.getDenuncia();

        // Eliminar comentario
        comentarioInternoRepository.delete(comentario);

        log.info("✅ Comentario interno eliminado por {}", funcionario.getUsername());

        // Registrar en historial
        registrarAccion(denuncia, funcionario, HistorialAccion.TipoAccion.COMENTARIO,
                "Comentario interno eliminado por funcionario " + funcionario.getNombre() + " "
                        + funcionario.getApellido());
    }

    /**
     * Obtener comentarios internos de una denuncia
     */
    @Transactional(readOnly = true)
    public List<ComentarioInternoResponse> obtenerComentariosInternos(Long denunciaId) {
        log.info("📋 Obteniendo comentarios internos de denuncia ID: {}", denunciaId);

        if (!denunciaRepository.existsById(denunciaId)) {
            throw new RuntimeException("Denuncia no encontrada con ID: " + denunciaId);
        }

        List<ComentarioInterno> comentarios = comentarioInternoRepository
                .findByDenunciaIdOrderByFechaComentarioDesc(denunciaId);

        log.info("✅ {} comentarios internos encontrados", comentarios.size());

        return comentarios.stream()
                .map(ComentarioInternoResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtener historial de acciones de una denuncia
     */
    @Transactional(readOnly = true)
    public List<HistorialAccionResponse> obtenerHistorialAcciones(Long denunciaId) {
        log.info("📜 Obteniendo historial de acciones de denuncia ID: {}", denunciaId);

        if (!denunciaRepository.existsById(denunciaId)) {
            throw new RuntimeException("Denuncia no encontrada con ID: " + denunciaId);
        }

        List<HistorialAccion> historial = historialAccionRepository
                .findByDenunciaIdOrderByFechaAccionDesc(denunciaId);

        log.info("✅ {} acciones encontradas en el historial", historial.size());

        return historial.stream()
                .map(HistorialAccionResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Registrar acción en el historial
     */
    private void registrarAccion(Denuncia denuncia, Usuario usuario,
            HistorialAccion.TipoAccion tipoAccion, String descripcion) {
        HistorialAccion historial = new HistorialAccion();
        historial.setDenuncia(denuncia);
        historial.setUsuario(usuario);
        historial.setTipoAccion(tipoAccion);
        historial.setDescripcion(descripcion);
        historial.setFechaAccion(LocalDateTime.now());

        historialAccionRepository.save(historial);

        log.info("📝 Acción registrada en historial: {} - {}", tipoAccion, descripcion);
    }

    /**
     * Listar todas las denuncias (para funcionarios)
     * Los funcionarios pueden ver TODAS las denuncias
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarTodasParaFuncionario(String emailFuncionario) {
        log.info("📋 Funcionario {} solicitando todas las denuncias", emailFuncionario);

        // Verificar que sea funcionario
        Usuario funcionario = usuarioRepository.findByEmail(emailFuncionario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!funcionario.getRol().getNombre().equals(Rol.FUNCIONARIO)) {
            throw new RuntimeException("Solo usuarios con rol FUNCIONARIO pueden acceder a esta función");
        }

        List<Denuncia> denuncias = denunciaRepository.findAllByOrderByFechaDenunciaDesc();

        log.info("✅ {} denuncias disponibles para revisión", denuncias.size());

        return denuncias.stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Listar denuncias revisadas por un funcionario específico
     */
    @Transactional(readOnly = true)
    public List<DenunciaResponse> listarDenunciasRevisadas(String emailFuncionario) {
        log.info("📋 Obteniendo denuncias revisadas por {}", emailFuncionario);

        Usuario funcionario = usuarioRepository.findByEmail(emailFuncionario)
                .orElseThrow(() -> new RuntimeException("Funcionario no encontrado"));

        List<Denuncia> denuncias = denunciaRepository.findByRevisorId(funcionario.getId());

        log.info("✅ {} denuncias revisadas", denuncias.size());

        return denuncias.stream()
                .map(DenunciaResponse::new)
                .collect(Collectors.toList());
    }
}