
package com.hospital.vidasalud.pacientes.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hospital.vidasalud.pacientes.entity.Paciente;

import reactor.core.publisher.Mono;

public interface PacienteRepository
        extends ReactiveCrudRepository<Paciente, Long> {

    Mono<Paciente> findByTipoDocumentoAndNumeroDocumento(
            String tipoDocumento,
            String numeroDocumento);

    Mono<Boolean> existsByTipoDocumentoAndNumeroDocumento(
            String tipoDocumento,
            String numeroDocumento);
}