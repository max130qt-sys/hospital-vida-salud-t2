package com.hospital.vidasalud.citas.rabbitmq;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CitaEventPublisher {

    private static final Logger logger =
            LoggerFactory.getLogger(CitaEventPublisher.class);

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final RabbitTemplate rabbitTemplate;

    public CitaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarCitaConfirmada(CitaConfirmadaEvent evento) {

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_CITAS,
                    RabbitMQConfig.ROUTING_KEY_CITA_CONFIRMADA,
                    evento
            );

            System.out.println();
            System.out.println("==================================================");
            System.out.println("       PUBLICACION DE EVENTO EN RABBITMQ");
            System.out.println("==================================================");
            System.out.println("Cita ID: " + evento.getCitaId());
            System.out.println("Paciente ID: " + evento.getPacienteId());
            System.out.println("Estado anterior: " + evento.getEstadoAnterior());
            System.out.println("Estado nuevo: " + evento.getEstadoNuevo());
            System.out.println("Especialidad: " + evento.getEspecialidad());
            System.out.println("Profesional: " + evento.getProfesionalCodigo());

            if (evento.getFechaHora() != null) {
                System.out.println("Fecha de la cita: "
                        + evento.getFechaHora().format(FORMATO_FECHA));
            }

            System.out.println("Intercambio: "
                    + RabbitMQConfig.EXCHANGE_CITAS);
            System.out.println("Clave de enrutamiento: "
                    + RabbitMQConfig.ROUTING_KEY_CITA_CONFIRMADA);
            System.out.println("Resultado: evento enviado a RabbitMQ");
            System.out.println("==================================================");
            System.out.println();

        } catch (RuntimeException e) {

            logger.error(
                    "Error al publicar el evento de la cita {}",
                    evento.getCitaId(),
                    e
            );

            System.err.println("ERROR AL PUBLICAR EL EVENTO EN RABBITMQ");
            System.err.println("Cita ID: " + evento.getCitaId());
            System.err.println("Detalle: " + e.getMessage());

            throw e;
        }
    }
}