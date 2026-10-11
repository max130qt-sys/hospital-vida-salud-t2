
package com.hospital.vidasalud.pacientes.service;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.hospital.vidasalud.pacientes.dto.PacienteRequest;
import com.hospital.vidasalud.pacientes.dto.PacienteResponse;
import com.hospital.vidasalud.pacientes.entity.Paciente;
import com.hospital.vidasalud.pacientes.exception.DocumentoDuplicadoException;
import com.hospital.vidasalud.pacientes.exception.RecursoNoEncontradoException;
import com.hospital.vidasalud.pacientes.repository.PacienteRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Mono<PacienteResponse> registrar(PacienteRequest request) {
        String tipoDocumento = normalizar(request.getTipoDocumento());
        String numeroDocumento = normalizar(request.getNumeroDocumento());

        return pacienteRepository
                .existsByTipoDocumentoAndNumeroDocumento(
                        tipoDocumento, numeroDocumento)
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new DocumentoDuplicadoException(
                                "Ya existe un paciente con ese tipo y número de documento"));
                    }

                    Paciente paciente = new Paciente();
                    copiarDatos(request, paciente);
                    paciente.setTipoDocumento(tipoDocumento);
                    paciente.setNumeroDocumento(numeroDocumento);
                    paciente.setEstado("ACTIVO");

                    return pacienteRepository.save(paciente)
                            .map(this::convertirAResponse);
                });
    }

    @Override
    public Flux<PacienteResponse> listar() {
        return pacienteRepository.findAll()
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<PacienteResponse> buscarPorId(Long id) {
        return obtenerPaciente(id)
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<PacienteResponse> buscarPorDocumento(
            String tipoDocumento, String numeroDocumento) {

        return pacienteRepository
                .findByTipoDocumentoAndNumeroDocumento(
                        normalizar(tipoDocumento),
                        normalizar(numeroDocumento))
                .switchIfEmpty(Mono.error(new RecursoNoEncontradoException(
                        "No se encontró un paciente con ese documento")))
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<PacienteResponse> actualizar(
            Long id, PacienteRequest request) {

        return obtenerPaciente(id)
                .flatMap(paciente -> {
                    String tipoDocumento =
                            normalizar(request.getTipoDocumento());
                    String numeroDocumento =
                            normalizar(request.getNumeroDocumento());

                    boolean cambioDocumento =
                            !tipoDocumento.equals(paciente.getTipoDocumento())
                            || !numeroDocumento.equals(
                                    paciente.getNumeroDocumento());

                    Mono<Boolean> duplicado = cambioDocumento
                            ? pacienteRepository
                                    .existsByTipoDocumentoAndNumeroDocumento(
                                            tipoDocumento, numeroDocumento)
                            : Mono.just(false);

                    return duplicado.flatMap(existe -> {
                        if (existe) {
                            return Mono.error(
                                    new DocumentoDuplicadoException(
                                            "Otro paciente ya tiene ese documento"));
                        }

                        copiarDatos(request, paciente);
                        paciente.setTipoDocumento(tipoDocumento);
                        paciente.setNumeroDocumento(numeroDocumento);

                        return pacienteRepository.save(paciente)
                                .map(this::convertirAResponse);
                    });
                });
    }

    @Override
    public Mono<PacienteResponse> cambiarEstado(Long id, String estado) {
        String estadoNormalizado = normalizar(estado);

        if (!"ACTIVO".equals(estadoNormalizado)
                && !"INACTIVO".equals(estadoNormalizado)) {
            return Mono.error(new IllegalArgumentException(
                    "El estado debe ser ACTIVO o INACTIVO"));
        }

        return obtenerPaciente(id)
                .flatMap(paciente -> {
                    paciente.setEstado(estadoNormalizado);

                    return pacienteRepository.save(paciente)
                            .map(this::convertirAResponse);
                });
    }

    @Override
    public Mono<Void> eliminar(Long id) {
        return obtenerPaciente(id)
                .flatMap(paciente -> pacienteRepository.delete(paciente));
    }

    private Mono<Paciente> obtenerPaciente(Long id) {
        return pacienteRepository.findById(id)
                .switchIfEmpty(Mono.error(new RecursoNoEncontradoException(
                        "No existe un paciente con ID " + id)));
    }

    private void copiarDatos(PacienteRequest request, Paciente paciente) {
        paciente.setNombres(normalizar(request.getNombres()));
        paciente.setApellidos(normalizar(request.getApellidos()));
        paciente.setFechaNacimiento(request.getFechaNacimiento());
        paciente.setTelefono(normalizarOpcional(request.getTelefono()));
        paciente.setCorreo(normalizarOpcional(request.getCorreo()));
        paciente.setDireccion(normalizarOpcional(request.getDireccion()));

        if (paciente.getFechaRegistro() == null
                && paciente.getId() == null) {
            paciente.setFechaRegistro(LocalDateTime.now());
        }
    }

    private PacienteResponse convertirAResponse(Paciente paciente) {
        PacienteResponse response = new PacienteResponse();

        response.setId(paciente.getId());
        response.setTipoDocumento(paciente.getTipoDocumento());
        response.setNumeroDocumento(paciente.getNumeroDocumento());
        response.setNombres(paciente.getNombres());
        response.setApellidos(paciente.getApellidos());
        response.setFechaNacimiento(paciente.getFechaNacimiento());
        response.setTelefono(paciente.getTelefono());
        response.setCorreo(paciente.getCorreo());
        response.setDireccion(paciente.getDireccion());
        response.setEstado(paciente.getEstado());
        response.setFechaRegistro(paciente.getFechaRegistro());

        return response;
    }

    private String normalizar(String valor) {
        return valor == null
                ? ""
                : valor.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}