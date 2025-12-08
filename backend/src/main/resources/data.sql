-- =====================================================
-- 1. ROLES
-- =====================================================
INSERT INTO roles (nombre, descripcion) VALUES
                                            ('CIUDADANO', 'Usuario ciudadano que puede registrar denuncias'),
                                            ('FUNCIONARIO', 'Funcionario municipal que puede revisar y gestionar denuncias');

-- =====================================================
-- 2. CATEGORÍAS
-- =====================================================
INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex) VALUES
                                                                            ('Exceso de Velocidad', 'Vehículos circulando a velocidad excesiva', 'VEL-001', TRUE, '#C70039'),
                                                                            ('Basura en Vía Pública', 'Arrojo de basura desde vehículos a la vía pública', 'AMB-001', TRUE, '#6C757D'),
                                                                            ('Estacionamiento Prohibido', 'Vehículos estacionados en lugares no habilitados', 'EST-001', TRUE, '#FF5733'),
                                                                            ('Vehículo Mal Estacionado', 'Vehículos mal ubicados que dificultan el tránsito', 'EST-002', TRUE, '#FFC300'),
                                                                            ('No Respetar Señal de PARE', 'Vehículo que no detiene su marcha ante señal reglamentaria de PARE', 'TRA-001', TRUE, '#FF0000'),
                                                                            ('No Respetar Luz Roja', 'Paso de semáforo en luz roja', 'SEM-001', TRUE, '#900C3F'),
                                                                            ('Conducir en Contravía', 'Vehículo circulando en sentido contrario', 'CON-001', TRUE, '#0D6EFD'),
                                                                            ('Obstrucción de Vía Pública', 'Vehículo obstruyendo tránsito o accesos públicos', 'OBS-001', TRUE, '#581845'),
                                                                            ('Vehículo Abandonado', 'Vehículo dejado sin custodia en la vía pública', 'VEH-001', TRUE, '#DAF7A6'),
                                                                            ('Otro', 'Infracción no clasificada en categorías anteriores', 'OTR-001', TRUE, '#6F42C1');

-- =====================================================
-- 3. USUARIOS DE PRUEBA
-- =====================================================

-- Usuario Funcionario (username: funcionario, password: prueba123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('funcionario', 'prueba123', 'Pedro', 'Funcionario', 'funcionario@municipalidad.cl', '912345678', '11111111-1',
     (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO'), TRUE, CURRENT_TIMESTAMP);


-- Usuario Ciudadano 1 (username: ciudadano1, password: prueba123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('ciudadano1', 'prueba123', 'Juan', 'Pérez', 'juan.perez@email.cl', '987654321', '22222222-2',
     (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP);

-- Usuario Ciudadano 2 (username: ciudadano2, password: prueba123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('ciudadano2', 'prueba123', 'María', 'González', 'maria.gonzalez@email.cl', '945678912', '33333333-3',
     (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP);

-- Usuario Ciudadano 3 (username: ciudadano3, password: prueba123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('ciudadano3', 'prueba123', 'Diego', 'Rodríguez', 'diego.rodriguez@email.cl', '956789123', '44444444-4',
     (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP);

-- Usuario Ciudadano 4 (username: ciudadano4, password: prueba123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('ciudadano4', 'prueba123', 'Carla', 'Martínez', 'carla.martinez@email.cl', '967891234', '55555555-5',
     (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP);

-- =====================================================
-- 4. DENUNCIAS DE PRUEBA (BÁSICAS) - COMPATIBLE H2
-- =====================================================

-- PENDIENTES
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia
) VALUES
      (
          (SELECT id FROM usuarios WHERE username = 'ciudadano1'),
          (SELECT id FROM categorias WHERE codigo = 'EST-001'),
          'Vehículo estacionado frente a entrada de garaje por más de 2 horas',
          'ABCD12', -33.4489, -70.6693, 'Av. Providencia 1234', 'Centro', 'Providencia',
          'PENDIENTE', DATEADD('HOUR', -2, CURRENT_TIMESTAMP)
      ),
      (
          (SELECT id FROM usuarios WHERE username = 'ciudadano2'),
          (SELECT id FROM categorias WHERE codigo = 'VEL-001'),
          'Auto circulando a alta velocidad en zona escolar durante horario de salida',
          'EFGH34', -33.4372, -70.6506, 'Calle Los Leones 890', 'Barrio Norte', 'Providencia',
          'PENDIENTE', DATEADD('HOUR', -1, CURRENT_TIMESTAMP)
      );


-- EN_REVISION
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia
) VALUES
    (
        (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
        (SELECT id FROM categorias WHERE codigo = 'EST-002'),
        'Camioneta en doble fila obstaculizando el tránsito en hora punta',
        'IJKL56', -33.4258, -70.6066, 'Av. Apoquindo 3456', 'Las Condes', 'Las Condes',
        'EN_REVISION', DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)
    );


-- VALIDADA
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia, fecha_validacion, revisor_id
) VALUES
    (
        (SELECT id FROM usuarios WHERE username = 'ciudadano4'),
        (SELECT id FROM categorias WHERE codigo = 'OBS-001'),
        'Vehículo bloqueando completamente rampa de acceso para sillas de ruedas',
        'MNOP78', -33.4419, -70.6542, 'Manuel Montt 567', 'Centro', 'Providencia',
        'VALIDADA', DATEADD('HOUR', -26, CURRENT_TIMESTAMP), DATEADD('HOUR', -20, CURRENT_TIMESTAMP),
        (SELECT id FROM usuarios WHERE username = 'funcionario')
    );


-- RECHAZADA
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia, fecha_validacion, revisor_id, motivo_rechazo
) VALUES
    (
        (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
        (SELECT id FROM categorias WHERE codigo = 'VEL-001'),
        'Auto que creo iba rápido',
        'WXYZ56', -33.4600, -70.6800, 'Calle Sin Nombre', 'Norte', 'Santiago',
        'RECHAZADA',
        DATEADD('DAY', -4, CURRENT_TIMESTAMP),
        DATEADD('DAY', -3, CURRENT_TIMESTAMP),
        (SELECT id FROM usuarios WHERE username = 'funcionario'),
        'Descripción muy vaga, sin evidencia suficiente de exceso de velocidad.'
    );

-- =====================================================
-- 5. COMENTARIOS INTERNOS (MÍNIMOS)
-- =====================================================
INSERT INTO comentarios_internos (denuncia_id, usuario_id, comentario, fecha_comentario) VALUES
                                                                                             ((SELECT id FROM denuncias WHERE patente = 'EFGH34'),
                                                                                              (SELECT id FROM usuarios WHERE username = 'funcionario'),
                                                                                              'Revisando evidencia fotográfica. Se requiere validación adicional.',
                                                                                              DATEADD('MINUTE', -45, CURRENT_TIMESTAMP)),

                                                                                             ((SELECT id FROM denuncias WHERE patente = 'MNOP78'),
                                                                                              (SELECT id FROM usuarios WHERE username = 'funcionario'),
                                                                                              'Evidencia clara, se valida la denuncia.',
                                                                                              DATEADD('HOUR', -20, CURRENT_TIMESTAMP));

-- =====================================================
-- 6. HISTORIAL DE ACCIONES (MÍNIMOS)
-- =====================================================
INSERT INTO historial_acciones (denuncia_id, usuario_id, tipo_accion, descripcion, fecha_accion) VALUES
                                                                                                     ((SELECT id FROM denuncias WHERE patente = 'ABCD12'),
                                                                                                      (SELECT id FROM usuarios WHERE username = 'ciudadano1'),
                                                                                                      'CREACION', 'Denuncia creada por ciudadano',
                                                                                                      DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),

                                                                                                     ((SELECT id FROM denuncias WHERE patente = 'IJKL56'),
                                                                                                      (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
                                                                                                      'CREACION', 'Denuncia creada por ciudadano',
                                                                                                      DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)),

                                                                                                     ((SELECT id FROM denuncias WHERE patente = 'MNOP78'),
                                                                                                      (SELECT id FROM usuarios WHERE username = 'funcionario'),
                                                                                                      'VALIDACION', 'Denuncia validada por funcionario',
                                                                                                      DATEADD('HOUR', -20, CURRENT_TIMESTAMP)),

                                                                                                     ((SELECT id FROM denuncias WHERE patente = 'WXYZ56'),
                                                                                                      (SELECT id FROM usuarios WHERE username = 'funcionario'),
                                                                                                      'RECHAZO', 'Denuncia rechazada por falta de evidencia',
                                                                                                      DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- =====================================================
-- 7. NOTIFICACIONES (MÍNIMAS)
-- =====================================================
INSERT INTO notificaciones (usuario_id, denuncia_id, tipo, titulo, mensaje, leida, fecha_creacion) VALUES
                                                                                                       ((SELECT id FROM usuarios WHERE username = 'ciudadano4'),
                                                                                                        (SELECT id FROM denuncias WHERE patente = 'MNOP78'),
                                                                                                        'DENUNCIA_VALIDADA', 'Denuncia Validada',
                                                                                                        'Su denuncia ha sido validada por el equipo de revisión.', TRUE,
                                                                                                        DATEADD('HOUR', -20, CURRENT_TIMESTAMP)),

                                                                                                       ((SELECT id FROM usuarios WHERE username = 'ciudadano3'),
                                                                                                        (SELECT id FROM denuncias WHERE patente = 'WXYZ56'),
                                                                                                        'DENUNCIA_RECHAZADA', 'Denuncia Rechazada',
                                                                                                        'Su denuncia ha sido rechazada por falta de evidencia suficiente.', TRUE,
                                                                                                        DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- =====================================================
-- 8. EVIDENCIAS (MÍNIMAS)
-- =====================================================
INSERT INTO evidencias (denuncia_id, tipo, ruta_archivo, nombre_archivo, mime_type, tamano_bytes, fecha_subida) VALUES
                                                                                                                    ((SELECT id FROM denuncias WHERE patente = 'ABCD12'),
                                                                                                                     'FOTO', '/uploads/evidencia-1.jpg', 'evidencia-1.jpg', 'image/jpeg', 45000,
                                                                                                                     DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),

                                                                                                                    ((SELECT id FROM denuncias WHERE patente = 'EFGH34'),
                                                                                                                     'FOTO', '/uploads/evidencia-2.jpg', 'evidencia-2.jpg', 'image/jpeg', 52000,
                                                                                                                     DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),

                                                                                                                    ((SELECT id FROM denuncias WHERE patente = 'IJKL56'),
                                                                                                                     'FOTO', '/uploads/evidencia-3.jpg', 'evidencia-3.jpg', 'image/jpeg', 48000,
                                                                                                                     DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)),

                                                                                                                    ((SELECT id FROM denuncias WHERE patente = 'MNOP78'),
                                                                                                                     'FOTO', '/uploads/evidencia-4.jpg', 'evidencia-4.jpg', 'image/jpeg', 51000,
                                                                                                                     DATEADD('HOUR', -3, CURRENT_TIMESTAMP));