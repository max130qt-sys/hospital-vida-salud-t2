
package com.hospital.vidasalud.atenciones.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ProblemDetail;

import jakarta.validation.Valid;

import com.hospital.vidasalud.atenciones.dto.AtencionRequest;
import com.hospital.vidasalud.atenciones.dto.AtencionResponse;
import com.hospital.vidasalud.atenciones.exception.AtencionInvalidaException;
import com.hospital.vidasalud.atenciones.exception.AtencionNoEncontradaException;
import com.hospital.vidasalud.atenciones.service.AtencionService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/atenciones")
public class AtencionController {

    private final AtencionService atencionService;

    public AtencionController(AtencionService atencionService) {
        this.atencionService = atencionService;
    }

    @PostMapping
    public Mono<ResponseEntity<AtencionResponse>> registrar(
            @Valid @RequestBody AtencionRequest request) {

        return atencionService.registrar(request)
                .map(atencion -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(atencion));
    }

    @GetMapping
    public Flux<AtencionResponse> listar(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long citaId) {

        if (pacienteId != null) {
            return atencionService.listarPorPaciente(pacienteId);
        }

        if (citaId != null) {
            return atencionService.listarPorCita(citaId);
        }

        return atencionService.listar();
    }

    @GetMapping("/{id}")
    public Mono<AtencionResponse> buscarPorId(@PathVariable Long id) {
        return atencionService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Mono<AtencionResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtencionRequest request) {

        return atencionService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public Mono<AtencionResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {

        return atencionService.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable Long id) {
        return atencionService.eliminar(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(
            AtencionNoEncontradaException.class)
    public ResponseEntity<String> manejarNoEncontrada(
            AtencionNoEncontradaException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(
            AtencionInvalidaException.class)
    public ResponseEntity<String> manejarInvalida(
            AtencionInvalidaException ex) {

        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}