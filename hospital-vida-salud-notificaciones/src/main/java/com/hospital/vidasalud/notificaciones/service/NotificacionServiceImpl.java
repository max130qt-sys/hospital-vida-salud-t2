package com.hospital.vidasalud.notificaciones.service;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.hospital.vidasalud.notificaciones.dto.NotificacionResponse;
import com.hospital.vidasalud.notificaciones.entity.Notificacion;
import com.hospital.vidasalud.notificaciones.exception.NotificacionNoEncontradaException;
import com.hospital.vidasalud.notificaciones.rabbitmq.CitaConfirmadaEvent;
import com.hospital.vidasalud.notificaciones.repository.NotificacionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionServiceImpl(
            NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public Mono<NotificacionResponse> procesarCitaConfirmada(
            CitaConfirmadaEvent evento) {

        if (evento == null || evento.getCitaId() == null
                || evento.getPacienteId() == null) {
            return Mono.error(new IllegalArgumentException(
                    "El evento debe contener el ID de la cita y del paciente."));
        }

        String eventoId = generarEventoId(evento);

        return notificacionRepository.findByEventoId(eventoId)
                .map(this::convertirAResponse)
                .switchIfEmpty(Mono.defer(() -> {
                    Notificacion notificacion = new Notificacion();

                    notificacion.setEventoId(eventoId);
                    notificacion.setPacienteId(evento.getPacienteId());
                    notificacion.setTipoEvento("CITA_CONFIRMADA");
                    notificacion.setCanal("SISTEMA");
                    notificacion.setDestinatario(null);
                    notificacion.setMensaje(
                            construirMensaje(evento));
                    notificacion.setEstado("PROCESADA");

                    return notificacionRepository.save(notificacion)
                            .map(this::convertirAResponse);
                }));
    }

    @Override
    public Flux<NotificacionResponse> listarNotificaciones() {
        return notificacionRepository.findAll()
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<NotificacionResponse> buscarPorId(Long id) {
        return notificacionRepository.findById(id)
                .map(this::convertirAResponse)
                .switchIfEmpty(Mono.error(
                        new NotificacionNoEncontradaException(
                                "No existe una notificación con ID " + id)));
    }

    @Override
    public Flux<NotificacionResponse> listarPorPaciente(Long pacienteId) {
        return notificacionRepository.findByPacienteId(pacienteId)
                .map(this::convertirAResponse);
    }

    @Override
    public Flux<NotificacionResponse> listarPorEstado(String estado) {
        return notificacionRepository.findByEstado(
                        estado.toUpperCase())
                .map(this::convertirAResponse);
    }

    private String generarEventoId(CitaConfirmadaEvent evento) {
        if (evento.getFechaEvento() != null) {
            return "CITA-" + evento.getCitaId()
                    + "-" + evento.getFechaEvento();
        }

        return "CITA-" + evento.getCitaId()
                + "-" + evento.getEstadoNuevo();
    }

    private String construirMensaje(CitaConfirmadaEvent evento) {
        String fecha = evento.getFechaHora() != null
                ? evento.getFechaHora().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "fecha por confirmar";

        String especialidad = evento.getEspecialidad() != null
                ? evento.getEspecialidad()
                : "especialidad no especificada";

        return "Su cita de " + especialidad
                + ", programada para el " + fecha
                + ", ha sido confirmada.";
    }

    private NotificacionResponse convertirAResponse(
            Notificacion notificacion) {

        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getEventoId(),
                notificacion.getPacienteId(),
                notificacion.getTipoEvento(),
                notificacion.getCanal(),
                notificacion.getDestinatario(),
                notificacion.getMensaje(),
                notificacion.getEstado(),
                notificacion.getFechaCreacion());
    }
}