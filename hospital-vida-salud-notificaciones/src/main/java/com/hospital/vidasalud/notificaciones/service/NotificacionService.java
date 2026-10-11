package com.hospital.vidasalud.notificaciones.service;

import com.hospital.vidasalud.notificaciones.dto.NotificacionResponse;
import com.hospital.vidasalud.notificaciones.rabbitmq.CitaConfirmadaEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificacionService {

    Mono<NotificacionResponse> procesarCitaConfirmada(
            CitaConfirmadaEvent evento);

    Flux<NotificacionResponse> listarNotificaciones();

    Mono<NotificacionResponse> buscarPorId(Long id);

    Flux<NotificacionResponse> listarPorPaciente(Long pacienteId);

    Flux<NotificacionResponse> listarPorEstado(String estado);
}