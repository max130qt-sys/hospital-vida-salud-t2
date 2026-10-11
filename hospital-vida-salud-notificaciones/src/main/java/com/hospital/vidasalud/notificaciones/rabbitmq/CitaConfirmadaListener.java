package com.hospital.vidasalud.notificaciones.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.hospital.vidasalud.notificaciones.config.RabbitMQConfig;
import com.hospital.vidasalud.notificaciones.service.NotificacionService;

@Component
public class CitaConfirmadaListener {

    private final NotificacionService notificacionService;

    public CitaConfirmadaListener(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NOTIFICACIONES)
    public void recibirCitaConfirmada(CitaConfirmadaEvent evento) {
        System.out.println("NOTIFICACIONES: evento recibido de RabbitMQ");
        System.out.println("Cita ID: " + evento.getCitaId());
        System.out.println("Paciente ID: " + evento.getPacienteId());

        notificacionService.procesarCitaConfirmada(evento).block();
    }
}