package com.hospital.vidasalud.citas.dto;

import java.time.LocalDateTime;

public class CitaResponse {

    private Long id;
    private Long pacienteId;
    private String especialidad;
    private LocalDateTime fechaHora;
    private String profesionalCodigo;
    private String motivoAdministrativo;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public CitaResponse() {
    }

    public CitaResponse(
            Long id,
            Long pacienteId,
            String especialidad,
            LocalDateTime fechaHora,
            String profesionalCodigo,
            String motivoAdministrativo,
            String estado,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaActualizacion) {

        this.id = id;
        this.pacienteId = pacienteId;
        this.especialidad = especialidad;
        this.fechaHora = fechaHora;
        this.profesionalCodigo = profesionalCodigo;
        this.motivoAdministrativo = motivoAdministrativo;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getProfesionalCodigo() {
        return profesionalCodigo;
    }

    public void setProfesionalCodigo(String profesionalCodigo) {
        this.profesionalCodigo = profesionalCodigo;
    }

    public String getMotivoAdministrativo() {
        return motivoAdministrativo;
    }

    public void setMotivoAdministrativo(String motivoAdministrativo) {
        this.motivoAdministrativo = motivoAdministrativo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}