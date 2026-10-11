package com.hospital.vidasalud.citas.exception;

public class CitaNoEncontradaException extends RuntimeException {

    public CitaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}