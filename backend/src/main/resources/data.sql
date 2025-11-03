-- =====================================================
-- SCRIPT DE INICIALIZACIÓN - H2 DATABASE
-- Sistema de Denuncias Municipales
-- =====================================================

-- =====================================================
-- 1. ROLES
-- =====================================================
INSERT INTO roles (nombre, descripcion) VALUES
                                            ('CIUDADANO', 'Usuario ciudadano que puede registrar denuncias'),
                                            ('ADMINISTRADOR', 'Administrador con acceso completo al sistema');

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
                                                                            ('Conducción Temeraria', 'Maniobras peligrosas en vía pública', 'CON-001', TRUE, '#DC3545');

-- =====================================================
-- 3. USUARIOS DE PRUEBA
-- NOTA: En producción estas contraseñas deben estar encriptadas con BCrypt
-- =====================================================

-- Usuario Administrador (username: admin, password: admin123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('admin', '{noop}admin123', 'Administrador', 'Sistema', 'admin@municipalidad.cl', '912345678', '11111111-1',
     (SELECT id FROM roles WHERE nombre = 'ADMINISTRADOR'), TRUE, CURRENT_TIMESTAMP);

-- Usuario Ciudadano (username: ciudadano1, password: user123)
INSERT INTO usuarios (username, password, nombre, apellido, email, telefono, rut, rol_id, activo, fecha_registro) VALUES
    ('ciudadano1', '{noop}user123', 'Juan', 'Pérez', 'juan.perez@email.cl', '987654321', '22222222-2',
     (SELECT id FROM roles WHERE nombre = 'CIUDADANO'), TRUE, CURRENT_TIMESTAMP);
