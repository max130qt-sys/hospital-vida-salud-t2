
package com.hospital.vidasalud.pacientes.service;

import com.hospital.vidasalud.pacientes.dto.PacienteRequest;
import com.hospital.vidasalud.pacientes.dto.PacienteResponse;
import com.hospital.vidasalud.pacientes.entity.Paciente;
import com.hospital.vidasalud.pacientes.exception.DocumentoDuplicadoException;
import com.hospital.vidasalud.pacientes.exception.RecursoNoEncontradoException;
import com.hospital.vidasalud.pacientes.repository.PacienteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    private Paciente paciente;
    private PacienteRequest request;

    @BeforeEach
    void prepararDatos() {
        paciente = new Paciente();
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

        request = new PacienteRequest();
        request.setTipoDocumento("DNI");
        request.setNumeroDocumento("12345678");
        request.setNombres("Juan");
        request.setApellidos("Perez");
        request.setFechaNacimiento(LocalDate.of(2000, 5, 10));
        request.setTelefono("987654321");
        request.setCorreo("juan@gmail.com");
        request.setDireccion("Lima");
    }

    @Test
    void registrarPacienteCorrectamente() {
        when(pacienteRepository.existsByTipoDocumentoAndNumeroDocumento(
                "DNI", "12345678")).thenReturn(Mono.just(false));

        when(pacienteRepository.save(any(Paciente.class)))
                .thenAnswer(invocacion -> {
                    Paciente guardado = invocacion.getArgument(0);
                    guardado.setId(1L);
                    return Mono.just(guardado);
                });

        StepVerifier.create(pacienteService.registrar(request))
                .assertNext(respuesta -> {
                    assertEquals(1L, respuesta.getId());
                    assertEquals("DNI", respuesta.getTipoDocumento());
                    assertEquals("12345678", respuesta.getNumeroDocumento());
                    assertEquals("JUAN", respuesta.getNombres());
                    assertEquals("ACTIVO", respuesta.getEstado());
                })
                .verifyComplete();

        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    void rechazarRegistroConDocumentoDuplicado() {
        when(pacienteRepository.existsByTipoDocumentoAndNumeroDocumento(
                "DNI", "12345678")).thenReturn(Mono.just(true));

        StepVerifier.create(pacienteService.registrar(request))
                .expectError(DocumentoDuplicadoException.class)
                .verify();

        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void listarPacientesCorrectamente() {
        when(pacienteRepository.findAll()).thenReturn(Flux.just(paciente));

        StepVerifier.create(pacienteService.listar())
                .assertNext(respuesta -> {
                    assertEquals(1L, respuesta.getId());
                    assertEquals("Juan", respuesta.getNombres());
                    assertEquals("ACTIVO", respuesta.getEstado());
                })
                .verifyComplete();
    }

    @Test
    void buscarPacientePorIdCorrectamente() {
        when(pacienteRepository.findById(1L)).thenReturn(Mono.just(paciente));

        StepVerifier.create(pacienteService.buscarPorId(1L))
                .assertNext(respuesta -> {
                    assertEquals(1L, respuesta.getId());
                    assertEquals("12345678", respuesta.getNumeroDocumento());
                })
                .verifyComplete();
    }

    @Test
    void generarErrorCuandoPacienteNoExiste() {
        when(pacienteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(pacienteService.buscarPorId(99L))
                .expectError(RecursoNoEncontradoException.class)
                .verify();
    }

    @Test
    void buscarPacientePorDocumentoCorrectamente() {
        when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento(
                "DNI", "12345678")).thenReturn(Mono.just(paciente));

        StepVerifier.create(
                pacienteService.buscarPorDocumento("DNI", "12345678"))
                .assertNext(respuesta -> {
                    assertEquals("Juan", respuesta.getNombres());
                    assertEquals("12345678", respuesta.getNumeroDocumento());
                })
                .verifyComplete();
    }

    @Test
    void cambiarEstadoDePacienteCorrectamente() {
        when(pacienteRepository.findById(1L)).thenReturn(Mono.just(paciente));
        when(pacienteRepository.save(any(Paciente.class)))
                .thenAnswer(invocacion ->
                        Mono.just(invocacion.getArgument(0)));

        StepVerifier.create(pacienteService.cambiarEstado(1L, "INACTIVO"))
                .assertNext(respuesta ->
                        assertEquals("INACTIVO", respuesta.getEstado()))
                .verifyComplete();
    }

    @Test
    void rechazarEstadoNoPermitido() {
        StepVerifier.create(pacienteService.cambiarEstado(1L, "BLOQUEADO"))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(pacienteRepository, never()).save(any(Paciente.class));
    }
}