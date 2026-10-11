package com.hospital.vidasalud.auditoria.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.hospital.vidasalud.auditoria.dto.AuditoriaResponse;
import com.hospital.vidasalud.auditoria.entity.Auditoria;
import com.hospital.vidasalud.auditoria.exception.AuditoriaNoEncontradaException;
import com.hospital.vidasalud.auditoria.exception.EventoAuditoriaInvalidoException;
import com.hospital.vidasalud.auditoria.rabbitmq.CitaConfirmadaEvent;
import com.hospital.vidasalud.auditoria.repository.AuditoriaRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public Mono<AuditoriaResponse> procesarCitaConfirmada(
            CitaConfirmadaEvent evento) {

        if (evento == null
                || evento.getCitaId() == null
                || evento.getPacienteId() == null) {
            return Mono.error(new EventoAuditoriaInvalidoException(
                    "El evento debe contener el ID de la cita y del paciente"));
        }

        if (evento.getEstadoNuevo() == null
                || !"CONFIRMADA".equalsIgnoreCase(evento.getEstadoNuevo())) {
            return Mono.error(new EventoAuditoriaInvalidoException(
                    "El evento recibido no corresponde a una cita confirmada"));
        }

        LocalDateTime fechaEvento = evento.getFechaEvento() != null
                ? evento.getFechaEvento()
                : LocalDateTime.now();

        String eventoId = "CITA-" + evento.getCitaId()
                + "-" + fechaEvento;

        return auditoriaRepository.findByEventoId(eventoId)
                .flatMap(auditoriaExistente ->
                        Mono.just(convertirADto(auditoriaExistente)))
                .switchIfEmpty(Mono.defer(() -> {
                    Auditoria auditoria = new Auditoria();

                    auditoria.setEventoId(eventoId);
                    auditoria.setCitaId(evento.getCitaId());
                    auditoria.setPacienteId(evento.getPacienteId());
                    auditoria.setTipoEvento("CITA_CONFIRMADA");
                    auditoria.setEstadoAnterior(evento.getEstadoAnterior());
                    auditoria.setEstadoNuevo(evento.getEstadoNuevo());
                    auditoria.setDescripcion(
                            "Se confirmó la cita de "
                            + (evento.getEspecialidad() != null
                                    ? evento.getEspecialidad()
                                    : "especialidad no especificada")
                            + (evento.getFechaHora() != null
                                    ? ", programada para " + evento.getFechaHora()
                                    : ""));
                    auditoria.setOrigen(
                            evento.getOrigen() != null
                                    ? evento.getOrigen()
                                    : "hospital-vida-salud-citas");
                    auditoria.setFechaEvento(fechaEvento);
                    auditoria.setFechaRegistro(LocalDateTime.now());

                    return auditoriaRepository.save(auditoria)
                            .map(this::convertirADto);
                }));
    }

    @Override
    public Flux<AuditoriaResponse> listarAuditorias() {
        return auditoriaRepository.findAll()
                .map(this::convertirADto);
    }

    @Override
    public Mono<AuditoriaResponse> buscarPorId(Long id) {
        return auditoriaRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new AuditoriaNoEncontradaException(
                                "No se encontró el registro de auditoría con ID " + id)))
                .map(this::convertirADto);
    }

    @Override
    public Flux<AuditoriaResponse> listarPorCita(Long citaId) {
        return auditoriaRepository.findByCitaId(citaId)
                .map(this::convertirADto);
    }

    @Override
    public Flux<AuditoriaResponse> listarPorPaciente(Long pacienteId) {
        return auditoriaRepository.findByPacienteId(pacienteId)
                .map(this::convertirADto);
    }

    @Override
    public Flux<AuditoriaResponse> listarPorTipoEvento(String tipoEvento) {
        return auditoriaRepository.findByTipoEvento(tipoEvento)
                .map(this::convertirADto);
    }

    @Override
    public Flux<AuditoriaResponse> listarPorEstadoNuevo(String estadoNuevo) {
        return auditoriaRepository.findByEstadoNuevo(estadoNuevo)
                .map(this::convertirADto);
    }

    private AuditoriaResponse convertirADto(Auditoria auditoria) {
        return new AuditoriaResponse(
                auditoria.getId(),
                auditoria.getEventoId(),
                auditoria.getCitaId(),
                auditoria.getPacienteId(),
                auditoria.getTipoEvento(),
                auditoria.getEstadoAnterior(),
                auditoria.getEstadoNuevo(),
                auditoria.getDescripcion(),
                auditoria.getOrigen(),
                auditoria.getFechaEvento(),
                auditoria.getFechaRegistro());
    }
}