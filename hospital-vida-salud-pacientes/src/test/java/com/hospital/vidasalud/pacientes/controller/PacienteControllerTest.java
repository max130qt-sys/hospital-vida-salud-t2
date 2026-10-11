
package com.hospital.vidasalud.pacientes.controller;

import com.hospital.vidasalud.pacientes.dto.PacienteRequest;
import com.hospital.vidasalud.pacientes.dto.PacienteResponse;
import com.hospital.vidasalud.pacientes.exception.GlobalExceptionHandler;
import com.hospital.vidasalud.pacientes.exception.RecursoNoEncontradoException;
import com.hospital.vidasalud.pacientes.service.PacienteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteControllerTest {

    @Mock
    private PacienteService pacienteService;

    private WebTestClient cliente;

    private PacienteResponse paciente;

    @BeforeEach
    void prepararPruebas() {
        PacienteController controlador = new PacienteController(pacienteService);

        cliente = WebTestClient.bindToController(controlador)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();

        paciente = new PacienteResponse();
        paciente.setId(1L);
        paciente.setTipoDocumento("DNI");
        paciente.setNumeroDocumento("12345678");
        paciente.setNombres("Juan");
        paciente.setApellidos("Perez");
        paciente.setFechaNacimiento(LocalDate.of(2000, 5, 10));
        paciente.setTelefono("987654321");
        paciente.setCorreo("juan@gmail.com");
        paciente.setDireccion("Lima");
        paciente.setEstado("ACTIVO");
    }

    @Test
    void registrarPacienteDevuelveCodigo201() {
        when(pacienteService.registrar(any(PacienteRequest.class)))
                .thenReturn(Mono.just(paciente));

        Map<String, Object> solicitud = Map.of(
                "tipoDocumento", "DNI",
                "numeroDocumento", "12345678",
                "nombres", "Juan",
                "apellidos", "Perez",
                "fechaNacimiento", "2000-05-10",
                "telefono", "987654321",
                "correo", "juan@gmail.com",
                "direccion", "Lima"
        );

        cliente.post()
                .uri("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(solicitud)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.nombres").isEqualTo("Juan")
                .jsonPath("$.estado").isEqualTo("ACTIVO");
    }

    @Test
    void listarPacientesDevuelveCodigo200() {
        when(pacienteService.listar()).thenReturn(Flux.just(paciente));

        cliente.get()
                .uri("/pacientes")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].nombres").isEqualTo("Juan");
    }

    @Test
    void buscarPacientePorIdDevuelveCodigo200() {
        when(pacienteService.buscarPorId(1L)).thenReturn(Mono.just(paciente));

        cliente.get()
                .uri("/pacientes/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.numeroDocumento").isEqualTo("12345678");
    }

    @Test
    void buscarPacienteInexistenteDevuelveCodigo404() {
        when(pacienteService.buscarPorId(99L))
                .thenReturn(Mono.error(
                        new RecursoNoEncontradoException("Paciente no encontrado")));

        cliente.get()
                .uri("/pacientes/99")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Paciente no encontrado");
    }

    @Test
    void buscarPacientePorDocumentoDevuelveCodigo200() {
        when(pacienteService.buscarPorDocumento("DNI", "12345678"))
                .thenReturn(Mono.just(paciente));

        cliente.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pacientes/documento")
                        .queryParam("tipoDocumento", "DNI")
                        .queryParam("numeroDocumento", "12345678")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.nombres").isEqualTo("Juan")
                .jsonPath("$.numeroDocumento").isEqualTo("12345678");
    }

    @Test
    void cambiarEstadoDevuelveCodigo200() {
        PacienteResponse pacienteInactivo = new PacienteResponse();
        pacienteInactivo.setId(1L);
        pacienteInactivo.setNombres("Juan");
        pacienteInactivo.setEstado("INACTIVO");

        when(pacienteService.cambiarEstado(1L, "INACTIVO"))
                .thenReturn(Mono.just(pacienteInactivo));

        cliente.patch()
                .uri("/pacientes/1/estado")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("estado", "INACTIVO"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.estado").isEqualTo("INACTIVO");
    }

    @Test
    void eliminarPacienteDevuelveCodigo204() {
        when(pacienteService.eliminar(1L)).thenReturn(Mono.empty());

        cliente.delete()
                .uri("/pacientes/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(pacienteService).eliminar(1L);
    }
}