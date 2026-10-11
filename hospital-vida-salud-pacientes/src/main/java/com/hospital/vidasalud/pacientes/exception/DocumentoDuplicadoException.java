package com.hospital.vidasalud.pacientes.exception;

public class DocumentoDuplicadoException extends RuntimeException {

    public DocumentoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}