package com.hospital.vidasalud.citas.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CitaRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser mayor que cero")
    private Long pacienteId;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 100, message = "La especialidad no puede superar los 100 caracteres")
    private String especialidad;

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    @Future(message = "La fecha de la cita debe ser futura")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El código del profesional es obligatorio")
    @Size(max = 50, message = "El código del profesional no puede superar los 50 caracteres")
    private String profesionalCodigo;

    @Size(max = 250, message = "El motivo administrativo no puede superar los 250 caracteres")
    private String motivoAdministrativo;

    public CitaRequest() {
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
}