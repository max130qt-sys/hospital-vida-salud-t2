
package com.hospital.vidasalud.citas.service;

import com.hospital.vidasalud.citas.dto.CitaRequest;
import com.hospital.vidasalud.citas.dto.CitaResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CitaService {

    Mono<CitaResponse> registrar(CitaRequest request);

    Flux<CitaResponse> listar();

    Mono<CitaResponse> buscarPorId(Long id);

    Flux<CitaResponse> listarPorPaciente(Long pacienteId);

    Flux<CitaResponse> listarPorEstado(String estado);

    Mono<CitaResponse> actualizar(Long id, CitaRequest request);

    Mono<CitaResponse> actualizarEstado(Long id, String estado);

    Mono<Void> eliminar(Long id);
}