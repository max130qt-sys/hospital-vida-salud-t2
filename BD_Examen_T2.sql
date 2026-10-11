CREATE DATABASE HOSPITAL_VIDA_SALUD;

USE HOSPITAL_VIDA_SALUD;

CREATE TABLE pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(20) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE,
    telefono VARCHAR(20),
    correo VARCHAR(150),
    direccion VARCHAR(250),
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tipo_documento, numero_documento)
);

CREATE TABLE citas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    profesional_codigo VARCHAR(50) NOT NULL,
    motivo_administrativo VARCHAR(250),
    estado ENUM(
        'PENDIENTE',
        'CONFIRMADA',
        'ATENDIDA',
        'CANCELADA',
        'AUSENTE'
    ) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_citas_paciente (paciente_id),
    INDEX idx_citas_profesional_fecha (profesional_codigo, fecha_hora),
    INDEX idx_citas_estado (estado)
);

CREATE TABLE notificaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id VARCHAR(100) NOT NULL UNIQUE,
    paciente_id BIGINT,
    tipo_evento VARCHAR(60) NOT NULL,
    canal VARCHAR(30) NOT NULL DEFAULT 'SISTEMA',
    destinatario VARCHAR(150),
    mensaje VARCHAR(500) NOT NULL,
    estado ENUM('PENDIENTE', 'PROCESADA', 'ERROR')
        NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id VARCHAR(100) NOT NULL UNIQUE,
    cita_id BIGINT NULL,
    paciente_id BIGINT NULL,
    tipo_evento VARCHAR(50) NULL,
    estado_anterior VARCHAR(30) NULL,
    estado_nuevo VARCHAR(30) NULL,
    descripcion VARCHAR(500) NULL,
    origen VARCHAR(100) NULL,
    fecha_evento DATETIME(6) NOT NULL,
    fecha_registro DATETIME(6) NOT NULL,
    INDEX idx_auditoria_cita (cita_id),
    INDEX idx_auditoria_paciente (paciente_id),
    INDEX idx_auditoria_fecha (fecha_evento)
);
CREATE TABLE atenciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id BIGINT NOT NULL,
    cita_id BIGINT NOT NULL,
    fecha_atencion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    area VARCHAR(100) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'REGISTRADA',
    observacion VARCHAR(300) NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_atenciones_paciente (paciente_id),
    INDEX idx_atenciones_cita (cita_id),
    INDEX idx_atenciones_estado (estado)
);