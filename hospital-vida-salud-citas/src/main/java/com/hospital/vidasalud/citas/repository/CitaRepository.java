package com.hospital.vidasalud.citas.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.hospital.vidasalud.citas.entity.Cita;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CitaRepository extends ReactiveCrudRepository<Cita, Long> {

    Flux<Cita> findByPacienteId(Long pacienteId);

    Flux<Cita> findByEstado(String estado);

    Flux<Cita> findByPacienteIdAndEstado(Long pacienteId, String estado);

    Mono<Boolean> existsByPacienteId(Long pacienteId);
}