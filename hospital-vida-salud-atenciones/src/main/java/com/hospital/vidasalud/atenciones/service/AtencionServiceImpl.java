package com.hospital.vidasalud.atenciones.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.hospital.vidasalud.atenciones.dto.AtencionRequest;
import com.hospital.vidasalud.atenciones.dto.AtencionResponse;
import com.hospital.vidasalud.atenciones.entity.Atencion;
import com.hospital.vidasalud.atenciones.exception.AtencionInvalidaException;
import com.hospital.vidasalud.atenciones.exception.AtencionNoEncontradaException;
import com.hospital.vidasalud.atenciones.repository.AtencionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AtencionServiceImpl implements AtencionService {

    private final AtencionRepository atencionRepository;
    private final WebClient.Builder webClientBuilder;

    private final List<String> estadosPermitidos = Arrays.asList(
            "REGISTRADA",
            "EN_CURSO",
            "FINALIZADA",
            "CANCELADA"
    );

    public AtencionServiceImpl(
            AtencionRepository atencionRepository,
            WebClient.Builder webClientBuilder) {

        this.atencionRepository = atencionRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<AtencionResponse> registrar(AtencionRequest request) {

        return validarPaciente(request.getPacienteId())
                .then(validarCita(
                        request.getCitaId(),
                        request.getPacienteId()))
                .then(atencionRepository
                        .findByCitaId(request.getCitaId())
                        .hasElements())
                .flatMap(existe -> {

                    if (existe) {
                        return Mono.<AtencionResponse>error(
                                new AtencionInvalidaException(
                                        "Ya existe una atención registrada para esta cita"));
                    }

                    Atencion atencion = new Atencion();

                    atencion.setPacienteId(request.getPacienteId());
                    atencion.setCitaId(request.getCitaId());
                    atencion.setArea(request.getArea().trim());
                    atencion.setObservacion(request.getObservacion());
                    atencion.setEstado("REGISTRADA");

                    LocalDateTime ahora = LocalDateTime.now();

                    if (request.getFechaAtencion() != null) {
                        atencion.setFechaAtencion(
                                request.getFechaAtencion());
                    } else {
                        atencion.setFechaAtencion(ahora);
                    }

                    atencion.setFechaCreacion(ahora);
                    atencion.setFechaActualizacion(ahora);

                    return atencionRepository.save(atencion)
                            .map(this::convertirAResponse);
                });
    }

    @Override
    public Flux<AtencionResponse> listar() {

        return atencionRepository.findAll()
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<AtencionResponse> buscarPorId(Long id) {

        return atencionRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new AtencionNoEncontradaException(
                                "No se encontró la atención con ID " + id)))
                .map(this::convertirAResponse);
    }

    @Override
    public Flux<AtencionResponse> listarPorPaciente(Long pacienteId) {

        return atencionRepository.findByPacienteId(pacienteId)
                .map(this::convertirAResponse);
    }

    @Override
    public Flux<AtencionResponse> listarPorCita(Long citaId) {

        return atencionRepository.findByCitaId(citaId)
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<AtencionResponse> actualizar(
            Long id,
            AtencionRequest request) {

        return atencionRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new AtencionNoEncontradaException(
                                "No se encontró la atención con ID " + id)))
                .flatMap(atencion ->
                        validarPaciente(request.getPacienteId())
                                .then(validarCita(
                                        request.getCitaId(),
                                        request.getPacienteId()))
                                .then(atencionRepository
                                        .findByCitaId(request.getCitaId())
                                        .filter(otra ->
                                                !otra.getId().equals(id))
                                        .hasElements())
                                .flatMap(existe -> {

                                    if (existe) {
                                        return Mono.<AtencionResponse>error(
                                                new AtencionInvalidaException(
                                                        "Ya existe otra atención registrada para esta cita"));
                                    }

                                    atencion.setPacienteId(
                                            request.getPacienteId());
                                    atencion.setCitaId(
                                            request.getCitaId());
                                    atencion.setArea(
                                            request.getArea().trim());
                                    atencion.setObservacion(
                                            request.getObservacion());

                                    if (request.getFechaAtencion() != null) {
                                        atencion.setFechaAtencion(
                                                request.getFechaAtencion());
                                    }

                                    atencion.setFechaActualizacion(
                                            LocalDateTime.now());

                                    return atencionRepository.save(atencion)
                                            .map(this::convertirAResponse);
                                }));
    }

    @Override
    public Mono<AtencionResponse> cambiarEstado(
            Long id,
            String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            return Mono.error(
                    new AtencionInvalidaException(
                            "Debe indicar el estado de la atención"));
        }

        String estadoNuevo = estado.trim().toUpperCase();

        if (!estadosPermitidos.contains(estadoNuevo)) {
            return Mono.error(
                    new AtencionInvalidaException(
                            "Estado no válido. Los estados permitidos son: "
                                    + estadosPermitidos));
        }

        return atencionRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new AtencionNoEncontradaException(
                                "No se encontró la atención con ID " + id)))
                .flatMap(atencion -> {

                    atencion.setEstado(estadoNuevo);
                    atencion.setFechaActualizacion(
                            LocalDateTime.now());

                    return atencionRepository.save(atencion)
                            .map(this::convertirAResponse);
                });
    }

    @Override
    public Mono<Void> eliminar(Long id) {

        return atencionRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new AtencionNoEncontradaException(
                                "No se encontró la atención con ID " + id)))
                .flatMap(atencion ->
                        atencionRepository.delete(atencion));
    }

    private Mono<Void> validarPaciente(Long pacienteId) {

        WebClient cliente = webClientBuilder.build();

        return cliente.get()
                .uri(
                        "http://hospital-vida-salud-pacientes/pacientes/{id}",
                        pacienteId)
                .retrieve()
                .bodyToMono(Map.class)
                .switchIfEmpty(Mono.<Map>error(
                        new AtencionInvalidaException(
                                "No se encontró el paciente")))
                .then()
                .onErrorMap(
                        WebClientResponseException.NotFound.class,
                        e -> new AtencionInvalidaException(
                                "No existe el paciente con ID " + pacienteId))
                .onErrorMap(
                        WebClientResponseException.class,
                        e -> new AtencionInvalidaException(
                                "No se pudo validar el paciente. Estado HTTP: "
                                        + e.getStatusCode().value()));
    }

    private Mono<Void> validarCita(
            Long citaId,
            Long pacienteId) {

        WebClient cliente = webClientBuilder.build();

        return cliente.get()
                .uri(
                        "http://hospital-vida-salud-citas/citas/{id}",
                        citaId)
                .retrieve()
                .bodyToMono(Map.class)
                .switchIfEmpty(Mono.<Map>error(
                        new AtencionInvalidaException(
                                "No se encontró la cita")))
                .flatMap(cita -> {

                    Object estado = cita.get("estado");
                    Object pacienteCita = cita.get("pacienteId");

                    if (estado == null
                            || !"CONFIRMADA".equalsIgnoreCase(
                                    estado.toString())) {

                        return Mono.<Void>error(
                                new AtencionInvalidaException(
                                        "La cita debe estar CONFIRMADA"));
                    }

                    if (pacienteCita == null
                            || !String.valueOf(pacienteCita)
                                    .equals(String.valueOf(pacienteId))) {

                        return Mono.<Void>error(
                                new AtencionInvalidaException(
                                        "La cita no pertenece al paciente indicado"));
                    }

                    return Mono.<Void>empty();
                })
                .onErrorMap(
                        WebClientResponseException.NotFound.class,
                        e -> new AtencionInvalidaException(
                                "No existe la cita con ID " + citaId))
                .onErrorMap(
                        WebClientResponseException.class,
                        e -> new AtencionInvalidaException(
                                "No se pudo validar la cita. Estado HTTP: "
                                        + e.getStatusCode().value()));
    }

    private AtencionResponse convertirAResponse(Atencion atencion) {

        return new AtencionResponse(
                atencion.getId(),
                atencion.getPacienteId(),
                atencion.getCitaId(),
                atencion.getFechaAtencion(),
                atencion.getArea(),
                atencion.getEstado(),
                atencion.getObservacion(),
                atencion.getFechaCreacion(),
                atencion.getFechaActualizacion()
        );
    }
}