
package com.hospital.vidasalud.notificaciones.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hospital.vidasalud.notificaciones.dto.NotificacionResponse;
import com.hospital.vidasalud.notificaciones.service.NotificacionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(
            NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public Flux<NotificacionResponse> listarNotificaciones(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) String estado) {

        if (pacienteId != null) {
            return notificacionService.listarPorPaciente(pacienteId);
        }

        if (estado != null && !estado.isBlank()) {
            return notificacionService.listarPorEstado(estado);
        }

        return notificacionService.listarNotificaciones();
    }

    @GetMapping("/{id}")
    public Mono<NotificacionResponse> buscarPorId(
            @PathVariable Long id) {

        return notificacionService.buscarPorId(id)
                .onErrorMap(
                        com.hospital.vidasalud.notificaciones.exception.NotificacionNoEncontradaException.class,
                        ex -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage()));
    }
}