
package com.hospital.vidasalud.auditoria.service;

import com.hospital.vidasalud.auditoria.dto.AuditoriaResponse;
import com.hospital.vidasalud.auditoria.rabbitmq.CitaConfirmadaEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuditoriaService {

    Mono<AuditoriaResponse> procesarCitaConfirmada(
            CitaConfirmadaEvent evento);

    Flux<AuditoriaResponse> listarAuditorias();

    Mono<AuditoriaResponse> buscarPorId(Long id);

    Flux<AuditoriaResponse> listarPorCita(Long citaId);

    Flux<AuditoriaResponse> listarPorPaciente(Long pacienteId);

    Flux<AuditoriaResponse> listarPorTipoEvento(String tipoEvento);

    Flux<AuditoriaResponse> listarPorEstadoNuevo(String estadoNuevo);
}