
package com.hospital.vidasalud.atenciones.service;

import com.hospital.vidasalud.atenciones.dto.AtencionRequest;
import com.hospital.vidasalud.atenciones.dto.AtencionResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AtencionService {

    Mono<AtencionResponse> registrar(AtencionRequest request);

    Flux<AtencionResponse> listar();

    Mono<AtencionResponse> buscarPorId(Long id);

    Flux<AtencionResponse> listarPorPaciente(Long pacienteId);

    Flux<AtencionResponse> listarPorCita(Long citaId);

    Mono<AtencionResponse> actualizar(Long id, AtencionRequest request);

    Mono<AtencionResponse> cambiarEstado(Long id, String estado);

    Mono<Void> eliminar(Long id);
}