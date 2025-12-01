-- =====================================================
-- SCRIPT DE INICIALIZACIÓN - MYSQL 8.0
-- Sistema de Denuncias Municipales
-- CON LIMPIEZA PREVIA DE DATOS
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- =====================================================
-- LIMPIEZA DE DATOS EXISTENTES
-- =====================================================
DELETE FROM notificaciones;
DELETE FROM historial_acciones;
DELETE FROM comentarios_internos;
DELETE FROM evidencias;
DELETE FROM denuncias;
DELETE FROM usuarios;
DELETE FROM categorias;
DELETE FROM roles;

-- Reiniciar auto_increment (opcional)
ALTER TABLE roles AUTO_INCREMENT = 1;
ALTER TABLE categorias AUTO_INCREMENT = 1;
ALTER TABLE usuarios AUTO_INCREMENT = 1;
ALTER TABLE denuncias AUTO_INCREMENT = 1;
ALTER TABLE evidencias AUTO_INCREMENT = 1;
ALTER TABLE comentarios_internos AUTO_INCREMENT = 1;
ALTER TABLE historial_acciones AUTO_INCREMENT = 1;
ALTER TABLE notificaciones AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

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
('Estacionamiento Indebido', 'Vehículos estacionados en lugares prohibidos', 'EST-001', TRUE, '#FF5733'),
('Exceso de Velocidad', 'Vehículos circulando a velocidad excesiva', 'VEL-001', TRUE, '#C70039'),
('Ruido Excesivo', 'Contaminación acústica vehicular', 'RUI-001', TRUE, '#900C3F'),
('Semáforo en Rojo', 'Paso de semáforo en luz roja', 'SEM-001', TRUE, '#FF0000'),
('Doble Fila', 'Estacionamiento en doble fila', 'EST-002', TRUE, '#FFC300'),
('Zona Peatonal', 'Circulación indebida en zona peatonal', 'ZON-001', TRUE, '#DAF7A6'),
('Sin Patente', 'Vehículo sin patente visible', 'VEH-001', TRUE, '#581845'),
('Basura en Vía Pública', 'Arrojo de basura desde vehículos', 'AMB-001', TRUE, '#6C757D'),
('Bloqueo de Rampa', 'Vehículo bloqueando rampa de accesibilidad', 'ACC-001', TRUE, '#0D6EFD'),
('Conducción Temeraria', 'Maniobras peligrosas en vía pública', 'CON-001', TRUE, '#DC3545'),
('Uso Indebido de Bocina', 'Uso excesivo o innecesario de bocina', 'RUI-002', TRUE, '#FD7E14'),
('Bloqueo de Cruce', 'Vehículo bloqueando paso peatonal', 'EST-003', TRUE, '#20C997'),
('Vehículo Abandonado', 'Vehículo sin uso prolongado en vía pública', 'VEH-002', TRUE, '#6F42C1'),
('Fuga de Combustible', 'Derrame de combustible en vía pública', 'AMB-002', TRUE, '#E83E8C'),
('Transporte Inseguro', 'Carga mal asegurada o sobresaliente', 'SEG-001', TRUE, '#FFC107');

-- =====================================================
-- 3. USUARIOS DE PRUEBA
-- =====================================================

-- Funcionarios (password: admin123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
('funcionario', 'prueba123', 'Pedro', 'Funcionario', 'funcionario@municipalidad.cl', '912345678', '11111111-1',
 (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO'), TRUE, NOW()),
('admin', 'prueba123', 'Carlos', 'Administrador', 'carlos.admin@municipalidad.cl', '922334455', '10000000-0',
 (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO'), TRUE, NOW()),
('supervisor', 'prueba123', 'Ana', 'Supervisor', 'ana.supervisor@municipalidad.cl', '933445566', '10111111-1',
 (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO'), TRUE, NOW()),
('inspector', 'prueba123', 'Luis', 'Inspector', 'luis.inspector@municipalidad.cl', '944556677', '10222222-2',
 (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO'), TRUE, NOW());

-- Ciudadanos (password: admin123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
('ciudadano1', 'prueba123', 'Juan', 'Pérez', 'juan.perez@email.cl', '987654321', '22222222-2',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano2', 'prueba123', 'María', 'González', 'maria.gonzalez@email.cl', '945678912', '33333333-3',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano3', 'prueba123', 'Diego', 'Rodríguez', 'diego.rodriguez@email.cl', '956789123', '44444444-4',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano4', 'prueba123', 'Carla', 'Martínez', 'carla.martinez@email.cl', '967891234', '55555555-5',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano5', 'prueba123', 'Roberto', 'Silva', 'roberto.silva@email.cl', '978912345', '66666666-6',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano6', 'prueba123', 'Fernanda', 'López', 'fernanda.lopez@email.cl', '989123456', '77777777-7',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano7', 'prueba123', 'Andrés', 'Muñoz', 'andres.munoz@email.cl', '991234567', '88888888-8',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano8', 'prueba123', 'Valentina', 'Herrera', 'valentina.herrera@email.cl', '992345678', '99999999-9',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano9', 'prueba123', 'Sebastián', 'Torres', 'sebastian.torres@email.cl', '993456789', '10333333-3',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW()),
('ciudadano10', 'prueba123', 'Camila', 'Flores', 'camila.flores@email.cl', '994567890', '10444444-4',
 (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, NOW());

-- =====================================================
-- 4. DENUNCIAS DE PRUEBA
-- =====================================================

-- Denuncias PENDIENTES
INSERT INTO denuncias (usuario_id, categoria_id, descripcion, patente, latitud, longitud, direccion, sector, comuna, estado, fecha_denuncia) VALUES
((SELECT id FROM usuarios WHERE username = 'ciudadano1'),
 (SELECT id FROM categorias WHERE codigo = 'EST-001'),
 'Vehículo estacionado frente a entrada de garaje por más de 2 horas',
 'ABCD12', -33.4489, -70.6693, 'Av. Providencia 1234', 'Centro', 'Providencia', 'PENDIENTE', DATE_SUB(NOW(), INTERVAL 2 HOUR)),

((SELECT id FROM usuarios WHERE username = 'ciudadano2'),
 (SELECT id FROM categorias WHERE codigo = 'VEL-001'),
 'Auto circulando a alta velocidad en zona escolar durante horario de salida',
 'EFGH34', -33.4372, -70.6506, 'Calle Los Leones 890', 'Barrio Norte', 'Providencia', 'PENDIENTE', DATE_SUB(NOW(), INTERVAL 1 HOUR)),

((SELECT id FROM usuarios WHERE username = 'ciudadano3'),
 (SELECT id FROM categorias WHERE codigo = 'EST-002'),
 'Camioneta en doble fila obstaculizando el tránsito en hora punta',
 'IJKL56', -33.4258, -70.6066, 'Av. Apoquindo 3456', 'Las Condes', 'Las Condes', 'PENDIENTE', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

((SELECT id FROM usuarios WHERE username = 'ciudadano4'),
 (SELECT id FROM categorias WHERE codigo = 'ACC-001'),
 'Vehículo bloqueando completamente rampa de acceso para sillas de ruedas',
 'MNOP78', -33.4419, -70.6542, 'Manuel Montt 567', 'Centro', 'Providencia', 'PENDIENTE', DATE_SUB(NOW(), INTERVAL 3 HOUR));

-- Denuncias EN_REVISION
INSERT INTO denuncias (usuario_id, categoria_id, descripcion, patente, latitud, longitud, direccion, sector, comuna, estado, fecha_denuncia) VALUES
((SELECT id FROM usuarios WHERE username = 'ciudadano5'),
 (SELECT id FROM categorias WHERE codigo = 'SEM-001'),
 'Taxista pasó semáforo en rojo con peatones cruzando',
 'QRST90', -33.4488, -70.6540, 'Pedro de Valdivia 234', 'Bellavista', 'Providencia', 'EN_REVISION', DATE_SUB(NOW(), INTERVAL 5 HOUR)),

((SELECT id FROM usuarios WHERE username = 'ciudadano6'),
 (SELECT id FROM categorias WHERE codigo = 'RUI-001'),
 'Moto con escape modificado generando ruido excesivo durante la madrugada',
 'UVWX12', -33.4300, -70.6100, 'Av. Kennedy 5678', 'Vitacura', 'Vitacura', 'EN_REVISION', DATE_SUB(NOW(), INTERVAL 4 HOUR)),

((SELECT id FROM usuarios WHERE username = 'ciudadano7'),
 (SELECT id FROM categorias WHERE codigo = 'ZON-001'),
 'Auto circulando en plaza peatonal poniendo en riesgo a niños jugando',
 'YZAB34', -33.4150, -70.6050, 'Plaza Los Dominicos', 'Las Condes Alto', 'Las Condes', 'EN_REVISION', DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- Denuncias VALIDADAS
INSERT INTO denuncias (usuario_id, categoria_id, descripcion, patente, latitud, longitud, direccion, sector, comuna, estado, fecha_denuncia, fecha_validacion, revisor_id) VALUES
((SELECT id FROM usuarios WHERE username = 'ciudadano8'),
 (SELECT id FROM categorias WHERE codigo = 'VEH-001'),
 'Vehículo circulando sin patente visible en ninguna parte',
 NULL, -33.4520, -70.6600, 'Av. Italia 2345', 'Ñuñoa', 'Ñuñoa', 'VALIDADA', 
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 20 HOUR),
 (SELECT id FROM usuarios WHERE username = 'funcionario')),

((SELECT id FROM usuarios WHERE username = 'ciudadano9'),
 (SELECT id FROM categorias WHERE codigo = 'AMB-001'),
 'Conductor arrojó bolsa de basura por la ventana en plena avenida',
 'CDEF56', -33.4400, -70.6550, 'Av. Grecia 789', 'Ñuñoa Centro', 'Ñuñoa', 'VALIDADA',
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 18 HOUR),
 (SELECT id FROM usuarios WHERE username = 'supervisor')),

((SELECT id FROM usuarios WHERE username = 'ciudadano10'),
 (SELECT id FROM categorias WHERE codigo = 'CON-001'),
 'Conductor realizando maniobras peligrosas y zigzagueando entre autos',
 'GHIJ78', -33.4450, -70.6450, 'Av. Vicuña Mackenna 1234', 'Macul', 'Macul', 'VALIDADA',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY),
 (SELECT id FROM usuarios WHERE username = 'inspector')),

((SELECT id FROM usuarios WHERE username = 'ciudadano1'),
 (SELECT id FROM categorias WHERE codigo = 'EST-003'),
 'Camión detenido sobre paso de cebra impidiendo cruce peatonal',
 'KLMN90', -33.4380, -70.6420, 'Av. Irarrázaval 3456', 'Ñuñoa', 'Ñuñoa', 'VALIDADA',
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 1 DAY), INTERVAL 6 HOUR),
 (SELECT id FROM usuarios WHERE username = 'funcionario')),

((SELECT id FROM usuarios WHERE username = 'ciudadano2'),
 (SELECT id FROM categorias WHERE codigo = 'RUI-002'),
 'Conductor usando bocina de manera agresiva y continua en atasco',
 'OPQR12', -33.4290, -70.6050, 'Av. El Bosque Norte 567', 'Las Condes', 'Las Condes', 'VALIDADA',
 DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY),
 (SELECT id FROM usuarios WHERE username = 'supervisor'));

-- Denuncias RECHAZADAS
INSERT INTO denuncias (usuario_id, categoria_id, descripcion, patente, latitud, longitud, direccion, sector, comuna, estado, fecha_denuncia, fecha_validacion, revisor_id, motivo_rechazo) VALUES
((SELECT id FROM usuarios WHERE username = 'ciudadano3'),
 (SELECT id FROM categorias WHERE codigo = 'EST-001'),
 'Auto estacionado en la calle',
 'STUV34', -33.4500, -70.6700, 'Calle Falsa 123', 'Desconocido', 'Santiago', 'RECHAZADA',
 DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 12 HOUR),
 (SELECT id FROM usuarios WHERE username = 'funcionario'),
 'La imagen no muestra claramente la infracción. El vehículo parece estar en zona permitida.'),

((SELECT id FROM usuarios WHERE username = 'ciudadano4'),
 (SELECT id FROM categorias WHERE codigo = 'VEL-001'),
 'Auto que creo iba rápido',
 'WXYZ56', -33.4600, -70.6800, 'Calle Sin Nombre', 'Norte', 'Santiago', 'RECHAZADA',
 DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY),
 (SELECT id FROM usuarios WHERE username = 'inspector'),
 'Descripción muy vaga, sin evidencia suficiente de exceso de velocidad.');

-- Denuncias CERRADAS
INSERT INTO denuncias (usuario_id, categoria_id, descripcion, patente, latitud, longitud, direccion, sector, comuna, estado, fecha_denuncia, fecha_validacion, revisor_id) VALUES
((SELECT id FROM usuarios WHERE username = 'ciudadano5'),
 (SELECT id FROM categorias WHERE codigo = 'VEH-002'),
 'Vehículo abandonado con ruedas desinfladas y vidrios rotos, lleva meses aquí',
 'AAAA11', -33.4550, -70.6650, 'Pasaje Los Olivos 45', 'La Reina', 'La Reina', 'CERRADA',
 DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY),
 (SELECT id FROM usuarios WHERE username = 'funcionario')),

((SELECT id FROM usuarios WHERE username = 'ciudadano6'),
 (SELECT id FROM categorias WHERE codigo = 'SEG-001'),
 'Camión transportando materiales sin asegurar, cayendo escombros en la vía',
 'BBBB22', -33.4470, -70.6580, 'Ruta 5 Sur Km 12', 'Industrial', 'San Bernardo', 'CERRADA',
 DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY),
 (SELECT id FROM usuarios WHERE username = 'supervisor'));

-- =====================================================
-- 5. COMENTARIOS INTERNOS
-- =====================================================

INSERT INTO comentarios_internos (denuncia_id, usuario_id, comentario, fecha_comentario) VALUES
((SELECT id FROM denuncias WHERE patente = 'QRST90'),
 (SELECT id FROM usuarios WHERE username = 'funcionario'),
 'Revisando evidencia fotográfica. Se requiere validación adicional.', DATE_ADD(DATE_SUB(NOW(), INTERVAL 4 HOUR), INTERVAL 30 MINUTE)),

((SELECT id FROM denuncias WHERE patente = 'UVWX12'),
 (SELECT id FROM usuarios WHERE username = 'supervisor'),
 'Coordinar con departamento de tránsito para seguimiento.', DATE_ADD(DATE_SUB(NOW(), INTERVAL 3 HOUR), INTERVAL 45 MINUTE)),

((SELECT id FROM denuncias WHERE patente = 'CDEF56'),
 (SELECT id FROM usuarios WHERE username = 'supervisor'),
 'Denuncia validada. Proceder con notificación al infractor.', DATE_SUB(NOW(), INTERVAL 18 HOUR)),

((SELECT id FROM denuncias WHERE patente = 'GHIJ78'),
 (SELECT id FROM usuarios WHERE username = 'inspector'),
 'Evidencia clara de conducción temeraria. Denuncia aprobada.', DATE_SUB(NOW(), INTERVAL 1 DAY)),

((SELECT id FROM denuncias WHERE patente = 'STUV34'),
 (SELECT id FROM usuarios WHERE username = 'funcionario'),
 'Imagen borrosa, no se aprecia claramente la infracción.', DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 12 HOUR));

-- =====================================================
-- 6. HISTORIAL DE ACCIONES
-- =====================================================

INSERT INTO historial_acciones (denuncia_id, usuario_id, tipo_accion, descripcion, fecha_accion) VALUES
((SELECT id FROM denuncias WHERE patente = 'CDEF56'),
 (SELECT id FROM usuarios WHERE username = 'ciudadano9'),
 'CREACION', 'Denuncia creada por ciudadano', DATE_SUB(NOW(), INTERVAL 1 DAY)),

((SELECT id FROM denuncias WHERE patente = 'CDEF56'),
 (SELECT id FROM usuarios WHERE username = 'supervisor'),
 'CAMBIO_ESTADO', 'Estado cambiado a EN_REVISION', DATE_SUB(NOW(), INTERVAL 22 HOUR)),

((SELECT id FROM denuncias WHERE patente = 'CDEF56'),
 (SELECT id FROM usuarios WHERE username = 'supervisor'),
 'COMENTARIO', 'Se agregó comentario interno', DATE_SUB(NOW(), INTERVAL 18 HOUR)),

((SELECT id FROM denuncias WHERE patente = 'CDEF56'),
 (SELECT id FROM usuarios WHERE username = 'supervisor'),
 'VALIDACION', 'Denuncia validada por supervisor', DATE_SUB(NOW(), INTERVAL 18 HOUR)),

((SELECT id FROM denuncias WHERE patente = 'GHIJ78'),
 (SELECT id FROM usuarios WHERE username = 'ciudadano10'),
 'CREACION', 'Denuncia creada por ciudadano', DATE_SUB(NOW(), INTERVAL 2 DAY)),

((SELECT id FROM denuncias WHERE patente = 'GHIJ78'),
 (SELECT id FROM usuarios WHERE username = 'inspector'),
 'VALIDACION', 'Denuncia validada tras revisión', DATE_SUB(NOW(), INTERVAL 1 DAY)),

((SELECT id FROM denuncias WHERE patente = 'STUV34'),
 (SELECT id FROM usuarios WHERE username = 'ciudadano3'),
 'CREACION', 'Denuncia creada', DATE_SUB(NOW(), INTERVAL 3 DAY)),

((SELECT id FROM denuncias WHERE patente = 'STUV34'),
 (SELECT id FROM usuarios WHERE username = 'funcionario'),
 'RECHAZO', 'Denuncia rechazada por falta de evidencia', DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 12 HOUR)),

((SELECT id FROM denuncias WHERE patente = 'AAAA11'),
 (SELECT id FROM usuarios WHERE username = 'ciudadano5'),
 'CREACION', 'Denuncia de vehículo abandonado', DATE_SUB(NOW(), INTERVAL 10 DAY)),

((SELECT id FROM denuncias WHERE patente = 'AAAA11'),
 (SELECT id FROM usuarios WHERE username = 'funcionario'),
 'VALIDACION', 'Denuncia validada', DATE_SUB(NOW(), INTERVAL 9 DAY)),

((SELECT id FROM denuncias WHERE patente = 'AAAA11'),
 (SELECT id FROM usuarios WHERE username = 'funcionario'),
 'CAMBIO_ESTADO', 'Caso cerrado - vehículo removido', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- =====================================================
-- 7. NOTIFICACIONES
-- =====================================================

INSERT INTO notificaciones (usuario_id, denuncia_id, tipo, titulo, mensaje, leida, fecha_creacion) VALUES
((SELECT id FROM usuarios WHERE username = 'ciudadano8'),
 (SELECT id FROM denuncias WHERE patente IS NULL AND estado = 'VALIDADA' LIMIT 1),
 'DENUNCIA_VALIDADA', 'Denuncia Validada',
 'Su denuncia sobre vehículo sin patente ha sido validada por el equipo de revisión.', TRUE, DATE_SUB(NOW(), INTERVAL 20 HOUR)),

((SELECT id FROM usuarios WHERE username = 'ciudadano9'),
 (SELECT id FROM denuncias WHERE patente = 'CDEF56'),
 'DENUNCIA_VALIDADA', 'Denuncia Validada',
 'Su denuncia sobre arrojo de basura ha sido validada y se procederá con las acciones correspondientes.', TRUE, DATE_SUB(NOW(), INTERVAL 18 HOUR)),

((SELECT id FROM usuarios WHERE username = 'ciudadano10'),
 (SELECT id FROM denuncias WHERE patente = 'GHIJ78'),
 'DENUNCIA_VALIDADA', 'Denuncia Validada',
 'Su denuncia sobre conducción temeraria ha sido validada.', FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY)),

((SELECT id FROM usuarios WHERE username = 'ciudadano3'),
 (SELECT id FROM denuncias WHERE patente = 'STUV34'),
 'DENUNCIA_RECHAZADA', 'Denuncia Rechazada',
 'Su denuncia ha sido rechazada. Motivo: La imagen no muestra claramente la infracción.', TRUE, DATE_ADD(DATE_SUB(NOW(), INTERVAL 2 DAY), INTERVAL 12 HOUR)),

((SELECT id FROM usuarios WHERE username = 'ciudadano4'),
 (SELECT id FROM denuncias WHERE patente = 'WXYZ56'),
 'DENUNCIA_RECHAZADA', 'Denuncia Rechazada',
 'Su denuncia ha sido rechazada por falta de evidencia suficiente.', FALSE, DATE_SUB(NOW(), INTERVAL 3 DAY)),

((SELECT id FROM usuarios WHERE username = 'ciudadano1'),
 NULL, 'SISTEMA', 'Bienvenido al Sistema',
 'Gracias por registrarte en el sistema de denuncias municipales.', TRUE, DATE_SUB(NOW(), INTERVAL 7 DAY)),

((SELECT id FROM usuarios WHERE username = 'ciudadano2'),
 NULL, 'SISTEMA', 'Actualización del Sistema',
 'El sistema estará en mantenimiento el próximo domingo de 02:00 a 06:00 hrs.', FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY)),

((SELECT id FROM usuarios WHERE username = 'funcionario'),
 NULL, 'SISTEMA', 'Nuevas Denuncias Pendientes',
 'Hay 4 denuncias pendientes de revisión en su bandeja.', FALSE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),

((SELECT id FROM usuarios WHERE username = 'funcionario'),
 (SELECT id FROM denuncias WHERE patente = 'QRST90'),
 'DENUNCIA_ASIGNADA', 'Nueva Denuncia Asignada',
 'Se le ha asignado una nueva denuncia para revisión: Semáforo en rojo.', TRUE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),

((SELECT id FROM usuarios WHERE username = 'supervisor'),
 (SELECT id FROM denuncias WHERE patente = 'UVWX12'),
 'DENUNCIA_ASIGNADA', 'Nueva Denuncia Asignada',
 'Se le ha asignado una nueva denuncia para revisión: Ruido excesivo.', FALSE, DATE_SUB(NOW(), INTERVAL 4 HOUR)),

((SELECT id FROM usuarios WHERE username = 'inspector'),
 (SELECT id FROM denuncias WHERE patente = 'YZAB34'),
 'DENUNCIA_ASIGNADA', 'Nueva Denuncia Asignada',
 'Se le ha asignado una nueva denuncia para revisión: Zona peatonal.', FALSE, DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- =====================================================
-- 8. EVIDENCIAS
-- =====================================================

INSERT INTO evidencias (denuncia_id, tipo, ruta_archivo, nombre_archivo, mime_type, tamano_bytes, fecha_subida) VALUES
((SELECT id FROM denuncias WHERE patente = 'ABCD12'), 'FOTO', '/uploads/evidencia-1.jpg', 'evidencia-1.jpg', 'image/jpeg', 45000, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
((SELECT id FROM denuncias WHERE patente = 'EFGH34'), 'FOTO', '/uploads/evidencia-2.jpg', 'evidencia-2.jpg', 'image/jpeg', 52000, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
((SELECT id FROM denuncias WHERE patente = 'EFGH34'), 'FOTO', '/uploads/evidencia-3.jpg', 'evidencia-3.jpg', 'image/jpeg', 48000, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
((SELECT id FROM denuncias WHERE patente = 'IJKL56'), 'FOTO', '/uploads/evidencia-4.jpg', 'evidencia-4.jpg', 'image/jpeg', 51000, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
((SELECT id FROM denuncias WHERE patente = 'MNOP78'), 'FOTO', '/uploads/evidencia-5.jpg', 'evidencia-5.jpg', 'image/jpeg', 47000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
((SELECT id FROM denuncias WHERE patente = 'MNOP78'), 'FOTO', '/uploads/evidencia-6.jpg', 'evidencia-6.jpg', 'image/jpeg', 49000, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
((SELECT id FROM denuncias WHERE patente = 'QRST90'), 'FOTO', '/uploads/evidencia-7.jpg', 'evidencia-7.jpg', 'image/jpeg', 53000, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
((SELECT id FROM denuncias WHERE patente = 'UVWX12'), 'FOTO', '/uploads/evidencia-8.jpg', 'evidencia-8.jpg', 'image/jpeg', 46000, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
((SELECT id FROM denuncias WHERE patente = 'YZAB34'), 'FOTO', '/uploads/evidencia-9.jpg', 'evidencia-9.jpg', 'image/jpeg', 50000, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
((SELECT id FROM denuncias WHERE patente IS NULL AND estado = 'VALIDADA' LIMIT 1), 'FOTO', '/uploads/evidencia-10.jpg', 'evidencia-10.jpg', 'image/jpeg', 44000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
((SELECT id FROM denuncias WHERE patente = 'CDEF56'), 'FOTO', '/uploads/evidencia-11.jpg', 'evidencia-11.jpg', 'image/jpeg', 48000, DATE_SUB(NOW(), INTERVAL 1 DAY)),
((SELECT id FROM denuncias WHERE patente = 'GHIJ78'), 'FOTO', '/uploads/evidencia-12.jpg', 'evidencia-12.jpg', 'image/jpeg', 55000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
((SELECT id FROM denuncias WHERE patente = 'GHIJ78'), 'FOTO', '/uploads/evidencia-13.jpg', 'evidencia-13.jpg', 'image/jpeg', 52000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
((SELECT id FROM denuncias WHERE patente = 'KLMN90'), 'FOTO', '/uploads/evidencia-14.jpg', 'evidencia-14.jpg', 'image/jpeg', 49000, DATE_SUB(NOW(), INTERVAL 2 DAY)),
((SELECT id FROM denuncias WHERE patente = 'OPQR12'), 'FOTO', '/uploads/evidencia-15.jpg', 'evidencia-15.jpg', 'image/jpeg', 47000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
((SELECT id FROM denuncias WHERE patente = 'STUV34'), 'FOTO', '/uploads/evidencia-16.jpg', 'evidencia-16.jpg', 'image/jpeg', 43000, DATE_SUB(NOW(), INTERVAL 3 DAY)),
((SELECT id FROM denuncias WHERE patente = 'WXYZ56'), 'FOTO', '/uploads/evidencia-17.jpg', 'evidencia-17.jpg', 'image/jpeg', 41000, DATE_SUB(NOW(), INTERVAL 4 DAY)),
((SELECT id FROM denuncias WHERE patente = 'AAAA11'), 'FOTO', '/uploads/evidencia-18.jpg', 'evidencia-18.jpg', 'image/jpeg', 54000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
((SELECT id FROM denuncias WHERE patente = 'AAAA11'), 'FOTO', '/uploads/evidencia-19.jpg', 'evidencia-19.jpg', 'image/jpeg', 56000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
((SELECT id FROM denuncias WHERE patente = 'AAAA11'), 'FOTO', '/uploads/evidencia-20.jpg', 'evidencia-20.jpg', 'image/jpeg', 53000, DATE_SUB(NOW(), INTERVAL 10 DAY)),
((SELECT id FROM denuncias WHERE patente = 'BBBB22'), 'FOTO', '/uploads/evidencia-21.jpg', 'evidencia-21.jpg', 'image/jpeg', 51000, DATE_SUB(NOW(), INTERVAL 15 DAY));

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================