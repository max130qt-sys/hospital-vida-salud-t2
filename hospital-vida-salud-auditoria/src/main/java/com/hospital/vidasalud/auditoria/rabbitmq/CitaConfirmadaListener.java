package com.hospital.vidasalud.auditoria.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hospital.vidasalud.auditoria.config.RabbitMQConfig;
import com.hospital.vidasalud.auditoria.service.AuditoriaService;

@Component
public class CitaConfirmadaListener {

    private final AuditoriaService auditoriaService;

    public CitaConfirmadaListener(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUDITORIA)
    public void recibirCitaConfirmada(CitaConfirmadaEvent evento) {

        System.out.println(
                "AUDITORIA: evento recibido de RabbitMQ");

        System.out.println("Cita ID: " + evento.getCitaId());
        System.out.println("Paciente ID: " + evento.getPacienteId());
        System.out.println("Estado anterior: " + evento.getEstadoAnterior());
        System.out.println("Estado nuevo: " + evento.getEstadoNuevo());

        auditoriaService.procesarCitaConfirmada(evento).block();
    }
}