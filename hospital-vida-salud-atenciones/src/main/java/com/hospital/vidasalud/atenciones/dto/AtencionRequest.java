
package com.hospital.vidasalud.atenciones.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AtencionRequest {

    @NotNull(message = "El ID del paciente es obligatorio")
    @Positive(message = "El ID del paciente debe ser positivo")
    private Long pacienteId;

    @NotNull(message = "El ID de la cita es obligatorio")
    @Positive(message = "El ID de la cita debe ser positivo")
    private Long citaId;

    private LocalDateTime fechaAtencion;

    @NotBlank(message = "El área es obligatoria")
    private String area;

    private String observacion;

    public AtencionRequest() {
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}