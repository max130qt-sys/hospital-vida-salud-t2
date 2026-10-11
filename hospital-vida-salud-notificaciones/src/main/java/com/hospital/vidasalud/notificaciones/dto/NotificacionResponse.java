package com.hospital.vidasalud.notificaciones.dto;

import java.time.LocalDateTime;

public class NotificacionResponse {

    private Long id;
    private String eventoId;
    private Long pacienteId;
    private String tipoEvento;
    private String canal;
    private String destinatario;
    private String mensaje;
    private String estado;
    private LocalDateTime fechaCreacion;

    public NotificacionResponse() {
    }

    public NotificacionResponse(Long id, String eventoId, Long pacienteId,
            String tipoEvento, String canal, String destinatario,
            String mensaje, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.eventoId = eventoId;
        this.pacienteId = pacienteId;
        this.tipoEvento = tipoEvento;
        this.canal = canal;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
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

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
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
}