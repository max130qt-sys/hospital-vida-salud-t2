
package com.hospital.vidasalud.auditoria.dto;

import java.time.LocalDateTime;

public class AuditoriaResponse {

    private Long id;
    private String eventoId;
    private Long citaId;
    private Long pacienteId;
    private String tipoEvento;
    private String estadoAnterior;
    private String estadoNuevo;
    private String descripcion;
    private String origen;
    private LocalDateTime fechaEvento;
    private LocalDateTime fechaRegistro;

    public AuditoriaResponse() {
    }

    public AuditoriaResponse(
            Long id,
            String eventoId,
            Long citaId,
            Long pacienteId,
            String tipoEvento,
            String estadoAnterior,
            String estadoNuevo,
            String descripcion,
            String origen,
            LocalDateTime fechaEvento,
            LocalDateTime fechaRegistro) {
        this.id = id;
        this.eventoId = eventoId;
        this.citaId = citaId;
        this.pacienteId = pacienteId;
        this.tipoEvento = tipoEvento;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.descripcion = descripcion;
        this.origen = origen;
        this.fechaEvento = fechaEvento;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventoId() {
        return eventoId;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }

    public Long getCitaId() {
        return citaId;
    }

    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}