package com.hospital.vidasalud.atenciones.controller;

import com.hospital.vidasalud.atenciones.dto.AtencionResponse;
import com.hospital.vidasalud.atenciones.service.AtencionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AtencionControllerTest {

    @Mock
    private AtencionService atencionService;

    private WebTestClient cliente;

    private AtencionResponse respuesta;

    @BeforeEach
    void configurar() {

        AtencionController controlador =
                new AtencionController(atencionService);

        cliente = WebTestClient
                .bindToController(controlador)
                .build();

        LocalDateTime ahora = LocalDateTime.now();

        respuesta = new AtencionResponse(
                1L,
                10L,
                20L,
                ahora,
                "Medicina general",
                "REGISTRADA",
                "Control médico",
                ahora,
                ahora
        );
    }

    @Test
    void listarAtencionesDevuelve200() {

        when(atencionService.listar())
                .thenReturn(Flux.just(respuesta));

        cliente.get()
                .uri("/atenciones")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].pacienteId").isEqualTo(10)
                .jsonPath("$[0].citaId").isEqualTo(20)
                .jsonPath("$[0].estado").isEqualTo("REGISTRADA");
    }

    @Test
    void buscarAtencionDevuelve200() {

        when(atencionService.buscarPorId(1L))
                .thenReturn(Mono.just(respuesta));

        cliente.get()
                .uri("/atenciones/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.pacienteId").isEqualTo(10)
                .jsonPath("$.citaId").isEqualTo(20);
    }

    @Test
    void eliminarAtencionDevuelve204() {

        when(atencionService.eliminar(1L))
                .thenReturn(Mono.empty());

        cliente.delete()
                .uri("/atenciones/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(atencionService).eliminar(1L);
    }
}