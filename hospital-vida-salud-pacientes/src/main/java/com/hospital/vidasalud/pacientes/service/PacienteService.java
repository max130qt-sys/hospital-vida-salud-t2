
package com.hospital.vidasalud.pacientes.service;

import com.hospital.vidasalud.pacientes.dto.PacienteRequest;
import com.hospital.vidasalud.pacientes.dto.PacienteResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PacienteService {

    Mono<PacienteResponse> registrar(PacienteRequest request);

    Flux<PacienteResponse> listar();

    Mono<PacienteResponse> buscarPorId(Long id);

    Mono<PacienteResponse> buscarPorDocumento(
            String tipoDocumento,
            String numeroDocumento);

    Mono<PacienteResponse> actualizar(Long id, PacienteRequest request);

    Mono<PacienteResponse> cambiarEstado(Long id, String estado);

    Mono<Void> eliminar(Long id);
}