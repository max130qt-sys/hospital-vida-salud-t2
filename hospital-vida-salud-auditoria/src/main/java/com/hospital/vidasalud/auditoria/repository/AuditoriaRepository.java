
package com.hospital.vidasalud.auditoria.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hospital.vidasalud.auditoria.entity.Auditoria;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuditoriaRepository
        extends ReactiveCrudRepository<Auditoria, Long> {

    Mono<Auditoria> findByEventoId(String eventoId);

    Flux<Auditoria> findByCitaId(Long citaId);

    Flux<Auditoria> findByPacienteId(Long pacienteId);

    Flux<Auditoria> findByTipoEvento(String tipoEvento);

    Flux<Auditoria> findByEstadoNuevo(String estadoNuevo);
}