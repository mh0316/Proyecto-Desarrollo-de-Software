-- =====================================================
-- SCRIPT DE DATOS DE PRUEBA
-- Compatible con H2 y MySQL
-- Con validaciones para evitar duplicados
-- =====================================================

-- 1. ROLES
INSERT INTO roles (nombre, descripcion)
SELECT 'CIUDADANO', 'Usuario ciudadano que puede registrar denuncias'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'CIUDADANO');

INSERT INTO roles (nombre, descripcion)
SELECT 'FUNCIONARIO', 'Funcionario municipal que puede revisar y gestionar denuncias'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'FUNCIONARIO');

-- 2. CATEGORÍAS
INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Exceso de Velocidad', 'Vehículos circulando a velocidad excesiva', 'VEL-001', TRUE, '#C70039'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'VEL-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Basura en Vía Pública', 'Arrojo de basura desde vehículos a la vía pública', 'AMB-001', TRUE, '#6C757D'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'AMB-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Estacionamiento Prohibido', 'Vehículos estacionados en lugares no habilitados', 'EST-001', TRUE, '#FF5733'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'EST-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Vehículo Mal Estacionado', 'Vehículos mal ubicados que dificultan el tránsito', 'EST-002', TRUE, '#FFC300'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'EST-002');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'No Respetar Señal de PARE', 'Vehículo que no detiene su marcha ante señal reglamentaria de PARE', 'TRA-001', TRUE, '#FF0000'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'TRA-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'No Respetar Luz Roja', 'Paso de semáforo en luz roja', 'SEM-001', TRUE, '#900C3F'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'SEM-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Conducir en Contravía', 'Vehículo circulando en sentido contrario', 'CON-001', TRUE, '#0D6EFD'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'CON-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Obstrucción de Vía Pública', 'Vehículo obstruyendo tránsito o accesos públicos', 'OBS-001', TRUE, '#581845'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'OBS-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Vehículo Abandonado', 'Vehículo dejado sin custodia en la vía pública', 'VEH-001', TRUE, '#DAF7A6'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'VEH-001');

INSERT INTO categorias (nombre, descripcion, codigo, activa, color_hex)
SELECT 'Otro', 'Infracción no clasificada en categorías anteriores', 'OTR-001', TRUE, '#6F42C1'
    WHERE NOT EXISTS (SELECT 1 FROM categorias WHERE codigo = 'OTR-001');

-- 3. USUARIOS DE PRUEBA
-- Contraseña para todos los usuarios de prueba: "prueba123" (hasheada con BCrypt)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro)
SELECT 'funcionario', '$2a$10$fOgptAKzW9DrJ46voLA8eeZiByJPAt6CUixSxef.e3pnz0v1eR8F6', 'Pedro', 'Funcionario', 'funcionario@municipalidad.cl', '912345678', '11111111-1',
       (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO'), TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'funcionario');

INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro)
SELECT 'ciudadano1', '$2a$10$fOgptAKzW9DrJ46voLA8eeZiByJPAt6CUixSxef.e3pnz0v1eR8F6', 'Juan', 'Pérez', 'juan.perez@email.cl', '987654321', '22222222-2',
       (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'ciudadano1');

INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro)
SELECT 'ciudadano2', '$2a$10$fOgptAKzW9DrJ46voLA8eeZiByJPAt6CUixSxef.e3pnz0v1eR8F6', 'María', 'González', 'maria.gonzalez@email.cl', '945678912', '33333333-3',
       (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'ciudadano2');

INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro)
SELECT 'ciudadano3', '$2a$10$fOgptAKzW9DrJ46voLA8eeZiByJPAt6CUixSxef.e3pnz0v1eR8F6', 'Diego', 'Rodríguez', 'diego.rodriguez@email.cl', '956789123', '44444444-4',
       (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'ciudadano3');

INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro)
SELECT 'ciudadano4', '$2a$10$fOgptAKzW9DrJ46voLA8eeZiByJPAt6CUixSxef.e3pnz0v1eR8F6', 'Carla', 'Martínez', 'carla.martinez@email.cl', '967891234', '55555555-5',
       (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'ciudadano4');

-- 4. DENUNCIAS DE PRUEBA (intervalos estándar compatibles)
-- PENDIENTES
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia
)
SELECT
    (SELECT id FROM usuarios WHERE username = 'ciudadano1'),
    (SELECT id FROM categorias WHERE codigo = 'EST-001'),
    'Vehículo estacionado frente a entrada de garaje por más de 2 horas',
    'ABCD12', -33.4489, -70.6693, 'Av. Providencia 1234', 'Centro', 'Providencia',
    'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '2' HOUR
    WHERE NOT EXISTS (SELECT 1 FROM denuncias WHERE patente = 'ABCD12');

INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia
)
SELECT
    (SELECT id FROM usuarios WHERE username = 'ciudadano2'),
    (SELECT id FROM categorias WHERE codigo = 'VEL-001'),
    'Auto circulando a alta velocidad en zona escolar durante horario de salida',
    'EFGH34', -33.4372, -70.6506, 'Calle Los Leones 890', 'Barrio Norte', 'Providencia',
    'PENDIENTE', CURRENT_TIMESTAMP - INTERVAL '1' HOUR
    WHERE NOT EXISTS (SELECT 1 FROM denuncias WHERE patente = 'EFGH34');

-- EN_REVISION
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia
)
SELECT
    (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
    (SELECT id FROM categorias WHERE codigo = 'EST-002'),
    'Camioneta en doble fila obstaculizando el tránsito en hora punta',
    'IJKL56', -33.4258, -70.6066, 'Av. Apoquindo 3456', 'Las Condes', 'Las Condes',
    'EN_REVISION', CURRENT_TIMESTAMP - INTERVAL '30' MINUTE
    WHERE NOT EXISTS (SELECT 1 FROM denuncias WHERE patente = 'IJKL56');

-- VALIDADA
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia, fecha_validacion, revisor_id
)
SELECT
    (SELECT id FROM usuarios WHERE username = 'ciudadano4'),
    (SELECT id FROM categorias WHERE codigo = 'OBS-001'),
    'Vehículo bloqueando completamente rampa de acceso para sillas de ruedas',
    'MNOP78', -33.4419, -70.6542, 'Manuel Montt 567', 'Centro', 'Providencia',
    'VALIDADA', CURRENT_TIMESTAMP - INTERVAL '26' HOUR, CURRENT_TIMESTAMP - INTERVAL '20' HOUR,
    (SELECT id FROM usuarios WHERE username = 'funcionario')
    WHERE NOT EXISTS (SELECT 1 FROM denuncias WHERE patente = 'MNOP78');

-- RECHAZADA
INSERT INTO denuncias (
    usuario_id, categoria_id, descripcion, patente,
    latitud, longitud, direccion, sector, comuna,
    estado, fecha_denuncia, fecha_validacion, revisor_id, motivo_rechazo
)
SELECT
    (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
    (SELECT id FROM categorias WHERE codigo = 'VEL-001'),
    'Auto que creo iba rápido',
    'WXYZ56', -33.4600, -70.6800, 'Calle Sin Nombre', 'Norte', 'Santiago',
    'RECHAZADA',
    CURRENT_TIMESTAMP - INTERVAL '4' DAY,
    CURRENT_TIMESTAMP - INTERVAL '3' DAY,
    (SELECT id FROM usuarios WHERE username = 'funcionario'),
    'Descripción muy vaga, sin evidencia suficiente de exceso de velocidad.'
    WHERE NOT EXISTS (SELECT 1 FROM denuncias WHERE patente = 'WXYZ56');

-- 5. COMENTARIOS INTERNOS
INSERT INTO comentarios_internos (denuncia_id, usuario_id, comentario, fecha_comentario)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'EFGH34'),
    (SELECT id FROM usuarios WHERE username = 'funcionario'),
    'Revisando evidencia fotográfica. Se requiere validación adicional.',
    CURRENT_TIMESTAMP - INTERVAL '45' MINUTE
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'EFGH34')
  AND NOT EXISTS (
    SELECT 1 FROM comentarios_internos ci
    WHERE ci.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'EFGH34')
      AND ci.comentario = 'Revisando evidencia fotográfica. Se requiere validación adicional.'
);

INSERT INTO comentarios_internos (denuncia_id, usuario_id, comentario, fecha_comentario)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'MNOP78'),
    (SELECT id FROM usuarios WHERE username = 'funcionario'),
    'Evidencia clara, se valida la denuncia.',
    CURRENT_TIMESTAMP - INTERVAL '20' HOUR
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'MNOP78')
  AND NOT EXISTS (
    SELECT 1 FROM comentarios_internos ci
    WHERE ci.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'MNOP78')
      AND ci.comentario = 'Evidencia clara, se valida la denuncia.'
);

-- 6. HISTORIAL DE ACCIONES
INSERT INTO historial_acciones (denuncia_id, usuario_id, tipo_accion, descripcion, fecha_accion)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'ABCD12'),
    (SELECT id FROM usuarios WHERE username = 'ciudadano1'),
    'CREACION', 'Denuncia creada por ciudadano',
    CURRENT_TIMESTAMP - INTERVAL '2' HOUR
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'ABCD12')
  AND NOT EXISTS (
    SELECT 1 FROM historial_acciones ha
    WHERE ha.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'ABCD12')
      AND ha.tipo_accion = 'CREACION'
);

INSERT INTO historial_acciones (denuncia_id, usuario_id, tipo_accion, descripcion, fecha_accion)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'IJKL56'),
    (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
    'CREACION', 'Denuncia creada por ciudadano',
    CURRENT_TIMESTAMP - INTERVAL '30' MINUTE
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'IJKL56')
  AND NOT EXISTS (
    SELECT 1 FROM historial_acciones ha
    WHERE ha.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'IJKL56')
      AND ha.tipo_accion = 'CREACION'
);

INSERT INTO historial_acciones (denuncia_id, usuario_id, tipo_accion, descripcion, fecha_accion)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'MNOP78'),
    (SELECT id FROM usuarios WHERE username = 'funcionario'),
    'VALIDACION', 'Denuncia validada por funcionario',
    CURRENT_TIMESTAMP - INTERVAL '20' HOUR
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'MNOP78')
  AND NOT EXISTS (
    SELECT 1 FROM historial_acciones ha
    WHERE ha.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'MNOP78')
      AND ha.tipo_accion = 'VALIDACION'
);

INSERT INTO historial_acciones (denuncia_id, usuario_id, tipo_accion, descripcion, fecha_accion)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'WXYZ56'),
    (SELECT id FROM usuarios WHERE username = 'funcionario'),
    'RECHAZO', 'Denuncia rechazada por falta de evidencia',
    CURRENT_TIMESTAMP - INTERVAL '3' DAY
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'WXYZ56')
  AND NOT EXISTS (
    SELECT 1 FROM historial_acciones ha
    WHERE ha.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'WXYZ56')
      AND ha.tipo_accion = 'RECHAZO'
);

-- 7. NOTIFICACIONES
INSERT INTO notificaciones (usuario_id, denuncia_id, tipo, titulo, mensaje, leida, fecha_creacion)
SELECT
    (SELECT id FROM usuarios WHERE username = 'ciudadano4'),
    (SELECT id FROM denuncias WHERE patente = 'MNOP78'),
    'DENUNCIA_VALIDADA', 'Denuncia Validada',
    'Su denuncia ha sido validada por el equipo de revisión.', TRUE,
    CURRENT_TIMESTAMP - INTERVAL '20' HOUR
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'MNOP78')
  AND NOT EXISTS (
    SELECT 1 FROM notificaciones n
    WHERE n.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'MNOP78')
      AND n.tipo = 'DENUNCIA_VALIDADA'
);

INSERT INTO notificaciones (usuario_id, denuncia_id, tipo, titulo, mensaje, leida, fecha_creacion)
SELECT
    (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
    (SELECT id FROM denuncias WHERE patente = 'WXYZ56'),
    'DENUNCIA_RECHAZADA', 'Denuncia Rechazada',
    'Su denuncia ha sido rechazada por falta de evidencia suficiente.', TRUE,
    CURRENT_TIMESTAMP - INTERVAL '3' DAY
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'WXYZ56')
  AND NOT EXISTS (
    SELECT 1 FROM notificaciones n
    WHERE n.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'WXYZ56')
      AND n.tipo = 'DENUNCIA_RECHAZADA'
);

-- 8. EVIDENCIAS
INSERT INTO evidencias (denuncia_id, tipo, ruta_archivo, nombre_archivo, mime_type, tamano_bytes, fecha_subida)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'ABCD12'),
    'FOTO', '/uploads/evidencia-1.jpg', 'evidencia-1.jpg', 'image/jpeg', 45000,
    CURRENT_TIMESTAMP - INTERVAL '2' HOUR
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'ABCD12')
  AND NOT EXISTS (
    SELECT 1 FROM evidencias e
    WHERE e.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'ABCD12')
      AND e.nombre_archivo = 'evidencia-1.jpg'
);

INSERT INTO evidencias (denuncia_id, tipo, ruta_archivo, nombre_archivo, mime_type, tamano_bytes, fecha_subida)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'EFGH34'),
    'FOTO', '/uploads/evidencia-2.jpg', 'evidencia-2.jpg', 'image/jpeg', 52000,
    CURRENT_TIMESTAMP - INTERVAL '1' HOUR
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'EFGH34')
  AND NOT EXISTS (
    SELECT 1 FROM evidencias e
    WHERE e.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'EFGH34')
      AND e.nombre_archivo = 'evidencia-2.jpg'
);

INSERT INTO evidencias (denuncia_id, tipo, ruta_archivo, nombre_archivo, mime_type, tamano_bytes, fecha_subida)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'IJKL56'),
    'FOTO', '/uploads/evidencia-3.jpg', 'evidencia-3.jpg', 'image/jpeg', 48000,
    CURRENT_TIMESTAMP - INTERVAL '30' MINUTE
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'IJKL56')
  AND NOT EXISTS (
    SELECT 1 FROM evidencias e
    WHERE e.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'IJKL56')
      AND e.nombre_archivo = 'evidencia-3.jpg'
);

INSERT INTO evidencias (denuncia_id, tipo, ruta_archivo, nombre_archivo, mime_type, tamano_bytes, fecha_subida)
SELECT
    (SELECT id FROM denuncias WHERE patente = 'MNOP78'),
    'FOTO', '/uploads/evidencia-4.jpg', 'evidencia-4.jpg', 'image/jpeg', 51000,
    CURRENT_TIMESTAMP - INTERVAL '3' HOUR
    WHERE EXISTS (SELECT 1 FROM denuncias WHERE patente = 'MNOP78')
  AND NOT EXISTS (
    SELECT 1 FROM evidencias e
    WHERE e.denuncia_id = (SELECT id FROM denuncias WHERE patente = 'MNOP78')
      AND e.nombre_archivo = 'evidencia-4.jpg'
);