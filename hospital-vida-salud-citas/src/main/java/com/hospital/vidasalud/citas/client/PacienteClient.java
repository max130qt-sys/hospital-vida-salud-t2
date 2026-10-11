package com.hospital.vidasalud.citas.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.hospital.vidasalud.citas.exception.PacienteNoEncontradoException;

import reactor.core.publisher.Mono;

@Component
public class PacienteClient {

    private final WebClient webClient;

    public PacienteClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://hospital-vida-salud-pacientes")
                .build();
    }

    public Mono<Boolean> existePaciente(Long pacienteId) {

        return webClient.get()
                .uri("/pacientes/{id}", pacienteId)
                .retrieve()
                .toBodilessEntity()
                .map(respuesta -> true)
                .onErrorResume(
                        WebClientResponseException.NotFound.class,
                        error -> Mono.just(false)
                );
    }
}