
package com.hospital.vidasalud.pacientes.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {

        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DocumentoDuplicadoException.class)
    public ResponseEntity<Map<String, String>> manejarDocumentoDuplicado(
            DocumentoDuplicadoException ex) {

        return respuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(
            WebExchangeBindException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getFieldErrors().forEach(error ->
                errores.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarSolicitudIncorrecta(
            IllegalArgumentException ex) {

        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, String>> manejarClaveDuplicada(
            DuplicateKeyException ex) {

        return respuesta(
                HttpStatus.CONFLICT,
                "Ya existe un registro con los datos únicos indicados");
    }

    private ResponseEntity<Map<String, String>> respuesta(
            HttpStatus estado, String mensaje) {

        Map<String, String> cuerpo = new HashMap<>();
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);

        return ResponseEntity.status(estado).body(cuerpo);
    }
}