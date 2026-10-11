package com.hospital.vidasalud.atenciones.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hospital.vidasalud.atenciones.entity.Atencion;

import reactor.core.publisher.Flux;

public interface AtencionRepository
        extends ReactiveCrudRepository<Atencion, Long> {

    Flux<Atencion> findByPacienteId(Long pacienteId);

    Flux<Atencion> findByCitaId(Long citaId);

    Flux<Atencion> findByEstado(String estado);
}