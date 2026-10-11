package com.hospital.vidasalud.citas.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CitaNoEncontradaException.class)
    public Mono<ResponseEntity<Map<String, String>>> manejarCitaNoEncontrada(
            CitaNoEncontradaException ex) {

        return Mono.just(crearRespuesta(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        ));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, String>>> manejarValidaciones(
            WebExchangeBindException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errores));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, String>>> manejarEntradaInvalida(
            ServerWebInputException ex) {

        return Mono.just(crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados no tienen un formato válido."
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, String>>> manejarArgumentoInvalido(
            IllegalArgumentException ex) {

        return Mono.just(crearRespuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> manejarErrorGeneral(
            Exception ex) {

        return Mono.just(crearRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno al procesar la solicitud."
        ));
    }

    private ResponseEntity<Map<String, String>> crearRespuesta(
            HttpStatus estado,
            String mensaje) {

        Map<String, String> cuerpo = new HashMap<>();
        cuerpo.put("mensaje", mensaje);

        return ResponseEntity.status(estado).body(cuerpo);
    }

@ExceptionHandler(PacienteNoEncontradoException.class)
public ResponseEntity<Map<String, String>> manejarPacienteNoEncontrado(
        PacienteNoEncontradoException ex) {

    Map<String, String> respuesta = new HashMap<>();
    respuesta.put("error", "Paciente no encontrado");
    respuesta.put("mensaje", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
}
}