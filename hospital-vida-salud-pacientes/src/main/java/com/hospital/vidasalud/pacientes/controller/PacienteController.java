
package com.hospital.vidasalud.pacientes.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.vidasalud.pacientes.dto.PacienteRequest;
import com.hospital.vidasalud.pacientes.dto.PacienteResponse;
import com.hospital.vidasalud.pacientes.service.PacienteService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pacientes")
@Validated
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public Mono<ResponseEntity<PacienteResponse>> registrar(
            @Valid @RequestBody PacienteRequest request) {

        return pacienteService.registrar(request)
                .map(paciente -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(paciente));
    }

    @GetMapping
    public Flux<PacienteResponse> listar() {
        return pacienteService.listar();
    }

    @GetMapping("/{id}")
    public Mono<PacienteResponse> buscarPorId(
            @PathVariable Long id) {

        return pacienteService.buscarPorId(id);
    }

    @GetMapping("/documento")
    public Mono<PacienteResponse> buscarPorDocumento(
            @RequestParam String tipoDocumento,
            @RequestParam String numeroDocumento) {

        return pacienteService.buscarPorDocumento(
                tipoDocumento, numeroDocumento);
    }

    @PutMapping("/{id}")
    public Mono<PacienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequest request) {

        return pacienteService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public Mono<PacienteResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestBody Map<String, String> datos) {

        String estado = datos.get("estado");

        if (estado == null || estado.isBlank()) {
            return Mono.error(new IllegalArgumentException(
                    "El campo estado es obligatorio"));
        }

        return pacienteService.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(
            @PathVariable Long id) {

        return pacienteService.eliminar(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}