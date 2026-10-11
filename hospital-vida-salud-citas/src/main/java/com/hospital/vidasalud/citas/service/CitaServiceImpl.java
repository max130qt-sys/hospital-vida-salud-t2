package com.hospital.vidasalud.citas.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.hospital.vidasalud.citas.client.PacienteClient;
import com.hospital.vidasalud.citas.dto.CitaRequest;
import com.hospital.vidasalud.citas.dto.CitaResponse;
import com.hospital.vidasalud.citas.entity.Cita;
import com.hospital.vidasalud.citas.exception.CitaNoEncontradaException;
import com.hospital.vidasalud.citas.exception.PacienteNoEncontradoException;
import com.hospital.vidasalud.citas.rabbitmq.CitaConfirmadaEvent;
import com.hospital.vidasalud.citas.rabbitmq.CitaEventPublisher;
import com.hospital.vidasalud.citas.repository.CitaRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteClient pacienteClient;
    private final CitaEventPublisher citaEventPublisher;

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE",
            "CONFIRMADA",
            "ATENDIDA",
            "CANCELADA",
            "AUSENTE"
    );

    public CitaServiceImpl(
            CitaRepository citaRepository,
            PacienteClient pacienteClient,
            CitaEventPublisher citaEventPublisher) {

        this.citaRepository = citaRepository;
        this.pacienteClient = pacienteClient;
        this.citaEventPublisher = citaEventPublisher;
    }

    @Override
    public Mono<CitaResponse> registrar(CitaRequest request) {

        return pacienteClient.existePaciente(request.getPacienteId())
                .flatMap(existe -> {

                    if (!existe) {
                        return Mono.error(
                                new PacienteNoEncontradoException(
                                        "No existe el paciente con ID "
                                                + request.getPacienteId()
                                )
                        );
                    }

                    LocalDateTime ahora = LocalDateTime.now();

                    Cita cita = new Cita();
                    cita.setPacienteId(request.getPacienteId());
                    cita.setEspecialidad(request.getEspecialidad());
                    cita.setFechaHora(request.getFechaHora());
                    cita.setProfesionalCodigo(
                            request.getProfesionalCodigo()
                    );
                    cita.setMotivoAdministrativo(
                            request.getMotivoAdministrativo()
                    );
                    cita.setEstado("PENDIENTE");
                    cita.setFechaCreacion(ahora);
                    cita.setFechaActualizacion(ahora);

                    return citaRepository.save(cita)
                            .map(this::convertirAResponse);
                });
    }

    @Override
    public Flux<CitaResponse> listar() {

        return citaRepository.findAll()
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<CitaResponse> buscarPorId(Long id) {

        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new CitaNoEncontradaException(
                                "No se encontró la cita con ID " + id
                        )
                ))
                .map(this::convertirAResponse);
    }

    @Override
    public Flux<CitaResponse> listarPorPaciente(Long pacienteId) {

        return citaRepository.findByPacienteId(pacienteId)
                .map(this::convertirAResponse);
    }

    @Override
    public Flux<CitaResponse> listarPorEstado(String estado) {

        validarEstado(estado);

        return citaRepository.findByEstado(
                        estado.trim().toUpperCase()
                )
                .map(this::convertirAResponse);
    }

    @Override
    public Mono<CitaResponse> actualizar(
            Long id,
            CitaRequest request) {

        return pacienteClient.existePaciente(request.getPacienteId())
                .flatMap(existe -> {

                    if (!existe) {
                        return Mono.error(
                                new PacienteNoEncontradoException(
                                        "No existe el paciente con ID "
                                                + request.getPacienteId()
                                )
                        );
                    }

                    return citaRepository.findById(id)
                            .switchIfEmpty(Mono.error(
                                    new CitaNoEncontradaException(
                                            "No se encontró la cita con ID "
                                                    + id
                                    )
                            ))
                            .flatMap(cita -> {

                                cita.setPacienteId(
                                        request.getPacienteId()
                                );
                                cita.setEspecialidad(
                                        request.getEspecialidad()
                                );
                                cita.setFechaHora(
                                        request.getFechaHora()
                                );
                                cita.setProfesionalCodigo(
                                        request.getProfesionalCodigo()
                                );
                                cita.setMotivoAdministrativo(
                                        request.getMotivoAdministrativo()
                                );
                                cita.setFechaActualizacion(
                                        LocalDateTime.now()
                                );

                                return citaRepository.save(cita);
                            })
                            .map(this::convertirAResponse);
                });
    }

    @Override
    public Mono<CitaResponse> actualizarEstado(
            Long id,
            String estado) {

        validarEstado(estado);

        String nuevoEstado = estado.trim().toUpperCase();

        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new CitaNoEncontradaException(
                                "No se encontró la cita con ID " + id
                        )
                ))
                .flatMap(cita -> {

                    String estadoAnterior = cita.getEstado();

                    if (estadoAnterior.equals(nuevoEstado)) {
                        return Mono.just(cita)
                                .map(this::convertirAResponse);
                    }

                    if (!transicionPermitida(
                            estadoAnterior,
                            nuevoEstado)) {

                        return Mono.error(
                                new IllegalArgumentException(
                                        "No se permite cambiar el estado "
                                                + estadoAnterior
                                                + " a "
                                                + nuevoEstado
                                )
                        );
                    }

                    cita.setEstado(nuevoEstado);
                    cita.setFechaActualizacion(LocalDateTime.now());

                    return citaRepository.save(cita)
                            .flatMap(citaGuardada -> {

                                if ("CONFIRMADA".equals(nuevoEstado)) {

                                    System.out.println(
                                            "SERVICIO: se va a confirmar "
                                                    + "la cita con ID "
                                                    + citaGuardada.getId()
                                    );

                                    CitaConfirmadaEvent evento =
                                            new CitaConfirmadaEvent(
                                                    citaGuardada.getId(),
                                                    citaGuardada.getPacienteId(),
                                                    citaGuardada.getEspecialidad(),
                                                    citaGuardada.getFechaHora(),
                                                    citaGuardada.getProfesionalCodigo(),
                                                    estadoAnterior,
                                                    nuevoEstado,
                                                    LocalDateTime.now(),
                                                    "hospital-vida-salud-citas"
                                            );

                                    return Mono.fromRunnable(() -> {

                                        System.out.println(
                                                "SERVICIO: llamando al "
                                                        + "publicador de RabbitMQ"
                                        );

                                        citaEventPublisher
                                                .publicarCitaConfirmada(
                                                        evento
                                                );

                                    })
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .thenReturn(
                                            convertirAResponse(citaGuardada)
                                    );
                                }

                                return Mono.just(
                                        convertirAResponse(citaGuardada)
                                );
                            });
                });
    }

    @Override
    public Mono<Void> eliminar(Long id) {

        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new CitaNoEncontradaException(
                                "No se encontró la cita con ID " + id
                        )
                ))
                .flatMap(cita -> citaRepository.delete(cita));
    }

    private boolean transicionPermitida(
            String estadoActual,
            String nuevoEstado) {

        if ("CANCELADA".equals(estadoActual)
                || "ATENDIDA".equals(estadoActual)
                || "AUSENTE".equals(estadoActual)) {
            return false;
        }

        if ("CONFIRMADA".equals(estadoActual)
                && "PENDIENTE".equals(nuevoEstado)) {
            return false;
        }

        return true;
    }

    private void validarEstado(String estado) {

        if (estado == null
                || !ESTADOS_VALIDOS.contains(
                        estado.trim().toUpperCase())) {

            throw new IllegalArgumentException(
                    "Estado no válido. Use: PENDIENTE, CONFIRMADA, "
                            + "ATENDIDA, CANCELADA o AUSENTE."
            );
        }
    }

    private CitaResponse convertirAResponse(Cita cita) {

        return new CitaResponse(
                cita.getId(),
                cita.getPacienteId(),
                cita.getEspecialidad(),
                cita.getFechaHora(),
                cita.getProfesionalCodigo(),
                cita.getMotivoAdministrativo(),
                cita.getEstado(),
                cita.getFechaCreacion(),
                cita.getFechaActualizacion()
        );
    }
}