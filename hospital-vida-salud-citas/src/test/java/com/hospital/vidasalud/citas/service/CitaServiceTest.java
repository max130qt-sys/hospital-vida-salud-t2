package com.hospital.vidasalud.citas.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.vidasalud.citas.client.PacienteClient;
import com.hospital.vidasalud.citas.dto.CitaRequest;
import com.hospital.vidasalud.citas.entity.Cita;
import com.hospital.vidasalud.citas.exception.CitaNoEncontradaException;
import com.hospital.vidasalud.citas.exception.PacienteNoEncontradoException;
import com.hospital.vidasalud.citas.rabbitmq.CitaConfirmadaEvent;
import com.hospital.vidasalud.citas.rabbitmq.CitaEventPublisher;
import com.hospital.vidasalud.citas.repository.CitaRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private PacienteClient pacienteClient;

    @Mock
    private CitaEventPublisher citaEventPublisher;

    @InjectMocks
    private CitaServiceImpl citaService;

    private Cita cita;
    private CitaRequest request;

    @BeforeEach
    void configurar() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(2);
        LocalDateTime ahora = LocalDateTime.now();

        cita = new Cita();
        cita.setId(1L);
        cita.setPacienteId(10L);
        cita.setEspecialidad("Cardiología");
        cita.setFechaHora(fecha);
        cita.setProfesionalCodigo("MED001");
        cita.setMotivoAdministrativo("Control médico");
        cita.setEstado("PENDIENTE");
        cita.setFechaCreacion(ahora);
        cita.setFechaActualizacion(ahora);

        request = new CitaRequest();
        request.setPacienteId(10L);
        request.setEspecialidad("Cardiología");
        request.setFechaHora(fecha);
        request.setProfesionalCodigo("MED001");
        request.setMotivoAdministrativo("Control médico");
    }

    @Test
    void registrarCitaCuandoPacienteExiste() {
        when(pacienteClient.existePaciente(10L))
                .thenReturn(Mono.just(true));

        when(citaRepository.save(any(Cita.class)))
                .thenAnswer(invocacion -> {
                    Cita guardada = invocacion.getArgument(0);
                    guardada.setId(1L);
                    return Mono.just(guardada);
                });

        StepVerifier.create(citaService.registrar(request))
                .assertNext(respuesta -> {
                    org.junit.jupiter.api.Assertions.assertEquals(
                            1L, respuesta.getId());
                    org.junit.jupiter.api.Assertions.assertEquals(
                            10L, respuesta.getPacienteId());
                    org.junit.jupiter.api.Assertions.assertEquals(
                            "PENDIENTE", respuesta.getEstado());
                    org.junit.jupiter.api.Assertions.assertEquals(
                            "Cardiología", respuesta.getEspecialidad());
                })
                .verifyComplete();

        verify(pacienteClient).existePaciente(10L);
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    void noRegistrarCitaCuandoPacienteNoExiste() {
        when(pacienteClient.existePaciente(10L))
                .thenReturn(Mono.just(false));

        StepVerifier.create(citaService.registrar(request))
                .expectError(PacienteNoEncontradoException.class)
                .verify();

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void listarCitas() {
        when(citaRepository.findAll())
                .thenReturn(Flux.just(cita));

        StepVerifier.create(citaService.listar())
                .expectNextMatches(respuesta ->
                        respuesta.getId().equals(1L)
                        && respuesta.getEstado().equals("PENDIENTE"))
                .verifyComplete();
    }

    @Test
    void buscarCitaExistente() {
        when(citaRepository.findById(1L))
                .thenReturn(Mono.just(cita));

        StepVerifier.create(citaService.buscarPorId(1L))
                .expectNextMatches(respuesta ->
                        respuesta.getId().equals(1L)
                        && respuesta.getPacienteId().equals(10L))
                .verifyComplete();
    }

    @Test
    void errorCuandoLaCitaNoExiste() {
        when(citaRepository.findById(99L))
                .thenReturn(Mono.empty());

        StepVerifier.create(citaService.buscarPorId(99L))
                .expectError(CitaNoEncontradaException.class)
                .verify();
    }

    @Test
    void listarCitasPorPaciente() {
        when(citaRepository.findByPacienteId(10L))
                .thenReturn(Flux.just(cita));

        StepVerifier.create(citaService.listarPorPaciente(10L))
                .expectNextMatches(respuesta ->
                        respuesta.getPacienteId().equals(10L))
                .verifyComplete();
    }

    @Test
    void listarCitasPorEstado() {
        when(citaRepository.findByEstado("PENDIENTE"))
                .thenReturn(Flux.just(cita));

        StepVerifier.create(citaService.listarPorEstado("pendiente"))
                .expectNextMatches(respuesta ->
                        respuesta.getEstado().equals("PENDIENTE"))
                .verifyComplete();
    }

    @Test
    void rechazarEstadoInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> citaService.listarPorEstado("EN_ESPERA")
        );
    }

    @Test
    void actualizarEstadoDeCitaYPublicarEvento() {
        when(citaRepository.findById(1L))
                .thenReturn(Mono.just(cita));

        when(citaRepository.save(any(Cita.class)))
                .thenAnswer(invocacion ->
                        Mono.just(invocacion.getArgument(0)));

        StepVerifier.create(
                citaService.actualizarEstado(1L, "CONFIRMADA"))
                .expectNextMatches(respuesta ->
                        respuesta.getEstado().equals("CONFIRMADA"))
                .verifyComplete();

        verify(citaRepository).save(any(Cita.class));

        verify(citaEventPublisher).publicarCitaConfirmada(
                any(CitaConfirmadaEvent.class));
    }

    @Test
    void eliminarCitaExistente() {
        when(citaRepository.findById(1L))
                .thenReturn(Mono.just(cita));

        when(citaRepository.delete(cita))
                .thenReturn(Mono.empty());

        StepVerifier.create(citaService.eliminar(1L))
                .verifyComplete();

        verify(citaRepository).delete(cita);
    }
    @Test
    void rechazarCambioDeEstadoDeCitaCancelada() {

        cita.setEstado("CANCELADA");

        when(citaRepository.findById(1L))
                .thenReturn(Mono.just(cita));

        StepVerifier.create(
                citaService.actualizarEstado(1L, "CONFIRMADA"))
                .expectErrorMatches(error ->
                        error instanceof IllegalArgumentException
                        && error.getMessage().contains(
                                "No se permite cambiar el estado CANCELADA a CONFIRMADA"))
                .verify();

        verify(citaRepository).findById(1L);

        verify(citaRepository, never()).save(any(Cita.class));

        verify(citaEventPublisher, never())
                .publicarCitaConfirmada(any(CitaConfirmadaEvent.class));
    }
}