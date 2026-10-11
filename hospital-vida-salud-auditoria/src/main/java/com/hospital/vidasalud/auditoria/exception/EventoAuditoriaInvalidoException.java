package com.hospital.vidasalud.auditoria.exception;

public class EventoAuditoriaInvalidoException extends RuntimeException {

    public EventoAuditoriaInvalidoException(String mensaje) {
        super(mensaje);
    }
}