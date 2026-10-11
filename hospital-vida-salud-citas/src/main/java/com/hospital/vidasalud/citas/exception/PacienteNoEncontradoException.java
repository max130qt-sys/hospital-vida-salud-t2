package com.hospital.vidasalud.citas.exception;

public class PacienteNoEncontradoException extends RuntimeException {

    public PacienteNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}