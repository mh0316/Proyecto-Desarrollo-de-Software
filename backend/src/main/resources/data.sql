-- =====================================================
-- SCRIPT DE INICIALIZACIÓN - H2 DATABASE
-- Sistema de Denuncias Municipales
-- =====================================================

-- =====================================================
-- 1. ROLES
-- =====================================================
INSERT IGNORE INTO roles (nombre, descripcion) VALUES
                                            ('CIUDADANO', 'Usuario ciudadano que puede registrar denuncias'),
                                            ('FUNCIONARIO', 'Funcionario municipal que puede revisar y gestionar denuncias');

-- =====================================================
-- 2. CATEGORÍAS
-- =====================================================
INSERT IGNORE INTO categorias (nombre, descripcion, codigo, activa, color_hex) VALUES
                                                                            ('Estacionamiento Indebido', 'Vehículos estacionados en lugares prohibidos', 'EST-001', TRUE, '#FF5733'),
                                                                            ('Exceso de Velocidad', 'Vehículos circulando a velocidad excesiva', 'VEL-001', TRUE, '#C70039'),
                                                                            ('Ruido Excesivo', 'Contaminación acústica vehicular', 'RUI-001', TRUE, '#900C3F'),
                                                                            ('Semáforo en Rojo', 'Paso de semáforo en luz roja', 'SEM-001', TRUE, '#FF0000'),
                                                                            ('Doble Fila', 'Estacionamiento en doble fila', 'EST-002', TRUE, '#FFC300'),
                                                                            ('Zona Peatonal', 'Circulación indebida en zona peatonal', 'ZON-001', TRUE, '#DAF7A6'),
                                                                            ('Sin Patente', 'Vehículo sin patente visible', 'VEH-001', TRUE, '#581845'),
                                                                            ('Basura en Vía Pública', 'Arrojo de basura desde vehículos', 'AMB-001', TRUE, '#6C757D'),
                                                                            ('Bloqueo de Rampa', 'Vehículo bloqueando rampa de accesibilidad', 'ACC-001', TRUE, '#0D6EFD'),
                                                                            ('Conducción Temeraria', 'Maniobras peligrosas en vía pública', 'CON-001', TRUE, '#DC3545');

-- =====================================================
-- 3. USUARIOS DE PRUEBA
-- =====================================================

-- Usuario Funcionario (username: funcionario, password: func123)
INSERT IGNORE INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('funcionario', '{noop}func123', 'Pedro', 'Funcionario', 'funcionario@municipalidad.cl', '912345678', '11111111-1',
     (SELECT id FROM roles WHERE nombre = 'FUNCIONARIO'), TRUE, CURRENT_TIMESTAMP);

-- Usuario Ciudadano 1 (username: ciudadano1, password: user123)
INSERT IGNORE INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('ciudadano1', '{noop}user123', 'Juan', 'Pérez', 'juan.perez@email.cl', '987654321', '22222222-2',
     (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP);

-- Usuario Ciudadano 2 (username: ciudadano2, password: user123)
INSERT IGNORE INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('ciudadano2', '{noop}user123', 'María', 'González', 'maria.gonzalez@email.cl', '945678912', '33333333-3',
     (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP);