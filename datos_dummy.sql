-- Script opcional para inserción manual de datos iniciales
-- Base de datos: saas_manager (3NF)

INSERT INTO proveedor_nube (nombre_plataforma, categoria_servicio) VALUES
('Amazon Web Services (AWS)', 'Infraestructura como Servicio (IaaS)'),
('Google Cloud Platform', 'Plataforma y Colaboración (PaaS / SaaS)'),
('Microsoft Azure', 'Nube Híbrida y Productividad'),
('GitHub Enterprise', 'Control de Versiones y CI/CD');

INSERT INTO licencia_software (id_proveedor, tipo_plan, costo_mensual, asientos_totales) VALUES
(1, 'AWS Enterprise Support & Compute Pack', 2500.00, 25),
(2, 'Google Workspace Enterprise Plus', 1200.50, 100),
(3, 'Microsoft 365 E5 & Azure AD Premium', 1850.00, 80),
(4, 'GitHub Enterprise Cloud + Copilot', 950.00, 40);

INSERT INTO asignacion_empleado (id_licencia, correo_empleado, fecha_asignacion, estatus_activo) VALUES
(1, 'carlos.martinez@empresa.com', '2026-01-10', TRUE),
(2, 'carlos.martinez@empresa.com', '2026-01-10', TRUE),
(2, 'maria.fernandez@empresa.com', '2026-02-01', TRUE),
(4, 'maria.fernandez@empresa.com', '2026-02-01', TRUE),
(3, 'roberto.gonzalez@empresa.com', '2026-03-15', TRUE),
(4, 'ana.lopes@empresa.com', '2026-04-20', FALSE);
