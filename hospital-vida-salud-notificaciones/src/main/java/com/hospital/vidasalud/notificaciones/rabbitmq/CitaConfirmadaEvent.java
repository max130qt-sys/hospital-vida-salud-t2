package com.hospital.vidasalud.notificaciones.rabbitmq;

import java.time.LocalDateTime;

public class CitaConfirmadaEvent {

    private Long citaId;
    private Long pacienteId;
    private String especialidad;
    private LocalDateTime fechaHora;
    private String profesionalCodigo;
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fechaEvento;
    private String origen;

    public CitaConfirmadaEvent() {
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

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }
}