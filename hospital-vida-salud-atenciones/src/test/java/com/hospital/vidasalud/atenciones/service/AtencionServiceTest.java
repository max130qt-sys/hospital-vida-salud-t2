package com.hospital.vidasalud.atenciones.service;

import com.hospital.vidasalud.atenciones.dto.AtencionRequest;
import com.hospital.vidasalud.atenciones.entity.Atencion;
import com.hospital.vidasalud.atenciones.exception.AtencionInvalidaException;
import com.hospital.vidasalud.atenciones.repository.AtencionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtencionServiceTest {

    @Mock
    private AtencionRepository atencionRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec solicitudUri;

    @Mock
    private WebClient.RequestHeadersSpec solicitudHeaders;

    @Mock
    private WebClient.ResponseSpec respuestaWeb;

    @InjectMocks
    private AtencionServiceImpl atencionService;

    private AtencionRequest request;

    @BeforeEach
    void prepararDatos() {
        request = new AtencionRequest();

        request.setPacienteId(10L);
        request.setCitaId(20L);
        request.setArea(" Medicina general ");
        request.setObservacion("Control médico");
        request.setFechaAtencion(LocalDateTime.now());
    }

    private void configurarRespuestasWeb(
            Map<String, Object> datosPaciente,
            Map<String, Object> datosCita) {

        when(webClientBuilder.build())
                .thenReturn(webClient);

        when(webClient.get())
                .thenReturn(solicitudUri);

        when(solicitudUri.uri(
                anyString(), any(Object[].class)))
                .thenReturn(solicitudHeaders);

        when(solicitudHeaders.retrieve())
                .thenReturn(respuestaWeb);

        when(respuestaWeb.bodyToMono(Map.class))
                .thenReturn(
                        Mono.just(datosPaciente),
                        Mono.just(datosCita)
                );
    }

    @Test
    void registrarAtencionCuandoPacienteYCitaSonValidos() {

        configurarRespuestasWeb(
                Map.of("id", 10),
                Map.of(
                        "id", 20,
                        "pacienteId", 10,
                        "estado", "CONFIRMADA"
                )
        );

        when(atencionRepository.findByCitaId(20L))
                .thenReturn(Flux.empty());

        when(atencionRepository.save(any(Atencion.class)))
                .thenAnswer(invocacion -> {
                    Atencion atencion =
                            invocacion.getArgument(0);

                    atencion.setId(1L);

                    return Mono.just(atencion);
                });

        StepVerifier.create(atencionService.registrar(request))
                .assertNext(respuesta -> {
                    assertEquals(1L, respuesta.getId());
                    assertEquals(10L, respuesta.getPacienteId());
                    assertEquals(20L, respuesta.getCitaId());
                    assertEquals(
                            "Medicina general",
                            respuesta.getArea()
                    );
                    assertEquals(
                            "REGISTRADA",
                            respuesta.getEstado()
                    );
                })
                .verifyComplete();

        verify(atencionRepository)
                .save(any(Atencion.class));
    }

    @Test
    void rechazarAtencionCuandoLaCitaNoEstaConfirmada() {

        configurarRespuestasWeb(
                Map.of("id", 10),
                Map.of(
                        "id", 20,
                        "pacienteId", 10,
                        "estado", "PENDIENTE"
                )
        );

        when(atencionRepository.findByCitaId(20L))
                .thenReturn(Flux.empty());

        StepVerifier.create(atencionService.registrar(request))
                .expectError(AtencionInvalidaException.class)
                .verify();

        verify(atencionRepository, never())
                .save(any(Atencion.class));
    }

    @Test
    void rechazarAtencionCuandoLaCitaPerteneceAOtroPaciente() {

        configurarRespuestasWeb(
                Map.of("id", 10),
                Map.of(
                        "id", 20,
                        "pacienteId", 99,
                        "estado", "CONFIRMADA"
                )
        );

        when(atencionRepository.findByCitaId(20L))
                .thenReturn(Flux.empty());

        StepVerifier.create(atencionService.registrar(request))
                .expectError(AtencionInvalidaException.class)
                .verify();

        verify(atencionRepository, never())
                .save(any(Atencion.class));
    }

    @Test
    void rechazarRegistroDuplicadoParaLaMismaCita() {

        configurarRespuestasWeb(
                Map.of("id", 10),
                Map.of(
                        "id", 20,
                        "pacienteId", 10,
                        "estado", "CONFIRMADA"
                )
        );

        Atencion existente = new Atencion();

        existente.setId(5L);
        existente.setPacienteId(10L);
        existente.setCitaId(20L);
        existente.setArea("Medicina general");
        existente.setEstado("REGISTRADA");

        when(atencionRepository.findByCitaId(20L))
                .thenReturn(Flux.just(existente));

        StepVerifier.create(atencionService.registrar(request))
                .expectErrorMatches(error ->
                        error instanceof AtencionInvalidaException
                        && error.getMessage().contains(
                                "Ya existe una atención registrada"
                        ))
                .verify();

        verify(atencionRepository, never())
                .save(any(Atencion.class));
    }
}