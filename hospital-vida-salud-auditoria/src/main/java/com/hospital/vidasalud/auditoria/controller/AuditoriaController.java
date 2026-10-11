
package com.hospital.vidasalud.auditoria.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hospital.vidasalud.auditoria.dto.AuditoriaResponse;
import com.hospital.vidasalud.auditoria.exception.AuditoriaNoEncontradaException;
import com.hospital.vidasalud.auditoria.service.AuditoriaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public Flux<AuditoriaResponse> listarAuditorias(
            @RequestParam(required = false) Long citaId,
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) String estadoNuevo) {

        if (citaId != null) {
            return auditoriaService.listarPorCita(citaId);
        }

        if (pacienteId != null) {
            return auditoriaService.listarPorPaciente(pacienteId);
        }

        if (tipoEvento != null && !tipoEvento.isBlank()) {
            return auditoriaService.listarPorTipoEvento(tipoEvento);
        }

        if (estadoNuevo != null && !estadoNuevo.isBlank()) {
            return auditoriaService.listarPorEstadoNuevo(estadoNuevo);
        }

        return auditoriaService.listarAuditorias();
    }

    @GetMapping("/{id}")
    public Mono<AuditoriaResponse> buscarPorId(@PathVariable Long id) {
        return auditoriaService.buscarPorId(id)
                .onErrorMap(
                        AuditoriaNoEncontradaException.class,
                        ex -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, ex.getMessage()));
    }
}