package com.hospital.vidasalud.auditoria.exception;

public class AuditoriaNoEncontradaException extends RuntimeException {

    public AuditoriaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}