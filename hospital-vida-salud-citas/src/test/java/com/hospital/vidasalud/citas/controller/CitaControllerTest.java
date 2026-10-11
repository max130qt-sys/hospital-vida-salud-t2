package com.hospital.vidasalud.citas.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hospital.vidasalud.citas.dto.CitaRequest;
import com.hospital.vidasalud.citas.dto.CitaResponse;
import com.hospital.vidasalud.citas.service.CitaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CitaControllerTest {

    @Mock
    private CitaService citaService;

    private WebTestClient webTestClient;

    private CitaResponse respuesta;

    @BeforeEach
    void configurar() {
        CitaController controller = new CitaController(citaService);

        webTestClient = WebTestClient
                .bindToController(controller)
                .build();

        LocalDateTime ahora = LocalDateTime.now();

        respuesta = new CitaResponse(
                1L,
                10L,
                "Cardiología",
                ahora.plusDays(2),
                "MED001",
                "Control médico",
                "PENDIENTE",
                ahora,
                ahora
        );
    }

    @Test
    void registrarCitaDevuelve201() {
        when(citaService.registrar(any(CitaRequest.class)))
                .thenReturn(Mono.just(respuesta));

        webTestClient.post()
                .uri("/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "pacienteId": 10,
                            "especialidad": "Cardiología",
                            "fechaHora": "%s",
                            "profesionalCodigo": "MED001",
                            "motivoAdministrativo": "Control médico"
                        }
                        """.formatted(LocalDateTime.now().plusDays(2)))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.pacienteId").isEqualTo(10)
                .jsonPath("$.estado").isEqualTo("PENDIENTE");
    }

    @Test
    void listarCitasDevuelve200() {
        when(citaService.listar())
                .thenReturn(Flux.just(respuesta));

        webTestClient.get()
                .uri("/citas")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].especialidad")
                .isEqualTo("Cardiología");
    }

    @Test
    void buscarCitaPorIdDevuelve200() {
        when(citaService.buscarPorId(1L))
                .thenReturn(Mono.just(respuesta));

        webTestClient.get()
                .uri("/citas/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.pacienteId").isEqualTo(10);
    }

    @Test
    void listarPorPacienteInvocaServicio() {
        when(citaService.listarPorPaciente(10L))
                .thenReturn(Flux.just(respuesta));

        webTestClient.get()
                .uri("/citas?pacienteId=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].pacienteId").isEqualTo(10);

        verify(citaService).listarPorPaciente(10L);
    }

    @Test
    void cambiarEstadoDeCita() {
        CitaResponse confirmada = new CitaResponse(
                1L,
                10L,
                "Cardiología",
                respuesta.getFechaHora(),
                "MED001",
                "Control médico",
                "CONFIRMADA",
                respuesta.getFechaCreacion(),
                LocalDateTime.now()
        );

        when(citaService.actualizarEstado(1L, "CONFIRMADA"))
                .thenReturn(Mono.just(confirmada));

        webTestClient.patch()
                .uri("/citas/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "estado": "CONFIRMADA"
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("CONFIRMADA");
    }

    @Test
    void eliminarCitaDevuelve204() {
        when(citaService.eliminar(1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/citas/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(citaService).eliminar(1L);
    }
}