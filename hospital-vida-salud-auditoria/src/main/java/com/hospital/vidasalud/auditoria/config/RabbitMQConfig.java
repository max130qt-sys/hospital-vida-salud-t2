
package com.hospital.vidasalud.auditoria.config;

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

    public static final String QUEUE_AUDITORIA =
            "hospital.auditoria.cita-confirmada";

    public static final String ROUTING_KEY_CITA_CONFIRMADA =
            "cita.confirmada";

    @Bean
    public DirectExchange citasExchange() {
        return new DirectExchange(EXCHANGE_CITAS, true, false);
    }

    @Bean
    public Queue auditoriaQueue() {
        return QueueBuilder.durable(QUEUE_AUDITORIA).build();
    }

    @Bean
    public Binding auditoriaBinding() {
        return BindingBuilder
                .bind(auditoriaQueue())
                .to(citasExchange())
                .with(ROUTING_KEY_CITA_CONFIRMADA);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}