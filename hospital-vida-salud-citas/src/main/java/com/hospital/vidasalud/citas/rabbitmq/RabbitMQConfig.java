package com.hospital.vidasalud.citas.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_CITAS =
            "hospital.citas.exchange";

    public static final String COLA_NOTIFICACIONES =
            "hospital.notificaciones.cita-confirmada";

    public static final String COLA_AUDITORIA =
            "hospital.auditoria.cita-confirmada";

    public static final String ROUTING_KEY_CITA_CONFIRMADA =
            "cita.confirmada";

    @Bean
    public DirectExchange exchangeCitas() {
        return new DirectExchange(EXCHANGE_CITAS, true, false);
    }

    @Bean
    public Queue colaNotificaciones() {
        return QueueBuilder.durable(COLA_NOTIFICACIONES).build();
    }

    @Bean
    public Queue colaAuditoria() {
        return QueueBuilder.durable(COLA_AUDITORIA).build();
    }

    @Bean
    public Binding bindingNotificaciones() {
        return BindingBuilder
                .bind(colaNotificaciones())
                .to(exchangeCitas())
                .with(ROUTING_KEY_CITA_CONFIRMADA);
    }

    @Bean
    public Binding bindingAuditoria() {
        return BindingBuilder
                .bind(colaAuditoria())
                .to(exchangeCitas())
                .with(ROUTING_KEY_CITA_CONFIRMADA);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}