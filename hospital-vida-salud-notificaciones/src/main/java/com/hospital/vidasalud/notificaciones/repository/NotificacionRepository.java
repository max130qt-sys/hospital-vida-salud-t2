
package com.hospital.vidasalud.notificaciones.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.hospital.vidasalud.notificaciones.entity.Notificacion;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificacionRepository
        extends ReactiveCrudRepository<Notificacion, Long> {

    Mono<Notificacion> findByEventoId(String eventoId);

    Flux<Notificacion> findByPacienteId(Long pacienteId);

    Flux<Notificacion> findByEstado(String estado);
}