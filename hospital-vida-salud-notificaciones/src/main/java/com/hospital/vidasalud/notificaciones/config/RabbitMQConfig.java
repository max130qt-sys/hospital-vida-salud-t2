
package com.hospital.vidasalud.notificaciones.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String EXCHANGE_CITAS =
            "hospital.citas.exchange";

    public static final String QUEUE_NOTIFICACIONES =
            "hospital.notificaciones.cita-confirmada";

    public static final String ROUTING_KEY_CITA_CONFIRMADA =
            "cita.confirmada";

    @Bean
    public DirectExchange citasExchange() {
        return new DirectExchange(EXCHANGE_CITAS, true, false);
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder
                .durable(QUEUE_NOTIFICACIONES)
                .build();
    }

    @Bean
    public Binding notificacionesBinding() {
        return BindingBuilder
                .bind(notificacionesQueue())
                .to(citasExchange())
                .with(ROUTING_KEY_CITA_CONFIRMADA);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}