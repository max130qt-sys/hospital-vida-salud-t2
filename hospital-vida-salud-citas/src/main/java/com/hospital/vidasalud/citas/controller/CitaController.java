package com.hospital.vidasalud.citas.controller;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.vidasalud.citas.dto.CitaRequest;
import com.hospital.vidasalud.citas.dto.CitaResponse;
import com.hospital.vidasalud.citas.service.CitaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CitaResponse> registrar(
            @Valid @RequestBody CitaRequest request) {

        return citaService.registrar(request);
    }

    @GetMapping
    public Flux<CitaResponse> listar(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) String estado) {

        if (pacienteId != null) {
            return citaService.listarPorPaciente(pacienteId);
        }

        if (estado != null && !estado.isBlank()) {
            return citaService.listarPorEstado(estado);
        }

        return citaService.listar();
    }

    @GetMapping("/{id}")
    public Mono<CitaResponse> buscarPorId(
            @PathVariable Long id) {

        return citaService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Mono<CitaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequest request) {

        return citaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public Mono<CitaResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> cuerpo) {

        return citaService.actualizarEstado(
                id,
                cuerpo.get("estado")
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> eliminar(@PathVariable Long id) {

        return citaService.eliminar(id);
    }
}