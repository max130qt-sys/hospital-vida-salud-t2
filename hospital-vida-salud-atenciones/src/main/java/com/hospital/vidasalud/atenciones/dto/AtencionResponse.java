package com.hospital.vidasalud.atenciones.dto;

import java.time.LocalDateTime;

public class AtencionResponse {

    private Long id;
    private Long pacienteId;
    private Long citaId;
    private LocalDateTime fechaAtencion;
    private String area;
    private String estado;
    private String observacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public AtencionResponse() {
    }

    public AtencionResponse(
            Long id,
            Long pacienteId,
            Long citaId,
            LocalDateTime fechaAtencion,
            String area,
            String estado,
            String observacion,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaActualizacion) {

        this.id = id;
        this.pacienteId = pacienteId;
        this.citaId = citaId;
        this.fechaAtencion = fechaAtencion;
        this.area = area;
        this.estado = estado;
        this.observacion = observacion;
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

    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDateTime fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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