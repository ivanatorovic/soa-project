package com.soa.tour_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PURCHASE_EXCHANGE = "purchase.exchange";
    public static final String TOURS_EXCHANGE = "tours.exchange";

    public static final String PURCHASE_STARTED_QUEUE = "purchase.started.queue";
    public static final String TOURS_RESERVED_QUEUE = "tours.reserved.queue";
    public static final String TOURS_RESERVATION_FAILED_QUEUE = "tours.reservation.failed.queue";
    public static final String TOURS_RESERVATION_CANCEL_QUEUE = "tours.reservation.cancel.queue";

    public static final String TOUR_EXECUTION_START_REQUESTED_QUEUE =
            "tour-execution-start-requested-queue";

    public static final String TOUR_EXECUTION_TOKEN_RESERVED_QUEUE =
            "tour-execution-token-reserved-queue";

    public static final String TOUR_EXECUTION_START_FAILED_QUEUE =
            "tour-execution-start-failed-queue";

    public static final String TOUR_EXECUTION_COMPLETED_QUEUE =
            "tour-execution-completed-queue";

    public static final String TOUR_EXECUTION_ABANDONED_QUEUE =
            "tour-execution-abandoned-queue";

    public static final String PURCHASE_STARTED_ROUTING_KEY = "purchase.started";
    public static final String TOURS_RESERVED_ROUTING_KEY = "tours.reserved";
    public static final String TOURS_RESERVATION_FAILED_ROUTING_KEY =
            "tours.reservation.failed";

    public static final String TOURS_RESERVATION_CANCEL_ROUTING_KEY =
            "tours.reservation.cancel";

    public static final String TOUR_EXECUTION_START_REQUESTED_ROUTING_KEY =
            "tour.execution.start.requested";

    public static final String TOUR_EXECUTION_TOKEN_RESERVED_ROUTING_KEY =
            "tour.execution.token.reserved";

    public static final String TOUR_EXECUTION_START_FAILED_ROUTING_KEY =
            "tour.execution.start.failed";

    public static final String TOUR_EXECUTION_COMPLETED_ROUTING_KEY =
            "tour.execution.completed";

    public static final String TOUR_EXECUTION_ABANDONED_ROUTING_KEY =
            "tour.execution.abandoned";

    @Bean
    public TopicExchange purchaseExchange() {
        return new TopicExchange(PURCHASE_EXCHANGE);
    }

    @Bean
    public TopicExchange toursExchange() {
        return new TopicExchange(TOURS_EXCHANGE);
    }

    @Bean
    public Queue purchaseStartedQueue() {
        return QueueBuilder.durable(PURCHASE_STARTED_QUEUE).build();
    }

    @Bean
    public Queue toursReservedQueue() {
        return QueueBuilder.durable(TOURS_RESERVED_QUEUE).build();
    }

    @Bean
    public Queue toursReservationFailedQueue() {
        return QueueBuilder.durable(TOURS_RESERVATION_FAILED_QUEUE).build();
    }

    @Bean
    public Queue toursReservationCancelQueue() {
        return QueueBuilder.durable(TOURS_RESERVATION_CANCEL_QUEUE).build();
    }

    @Bean
    public Queue tourExecutionStartRequestedQueue() {
        return QueueBuilder
                .durable(TOUR_EXECUTION_START_REQUESTED_QUEUE)
                .build();
    }

    @Bean
    public Queue tourExecutionTokenReservedQueue() {
        return QueueBuilder
                .durable(TOUR_EXECUTION_TOKEN_RESERVED_QUEUE)
                .build();
    }

    @Bean
    public Queue tourExecutionStartFailedQueue() {
        return QueueBuilder
                .durable(TOUR_EXECUTION_START_FAILED_QUEUE)
                .build();
    }

    @Bean
    public Queue tourExecutionCompletedQueue() {
        return QueueBuilder
                .durable(TOUR_EXECUTION_COMPLETED_QUEUE)
                .build();
    }

    @Bean
    public Queue tourExecutionAbandonedQueue() {
        return QueueBuilder
                .durable(TOUR_EXECUTION_ABANDONED_QUEUE)
                .build();
    }

    @Bean
    public Binding purchaseStartedBinding() {
        return BindingBuilder
                .bind(purchaseStartedQueue())
                .to(purchaseExchange())
                .with(PURCHASE_STARTED_ROUTING_KEY);
    }

    @Bean
    public Binding toursReservedBinding() {
        return BindingBuilder
                .bind(toursReservedQueue())
                .to(toursExchange())
                .with(TOURS_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Binding toursReservationFailedBinding() {
        return BindingBuilder
                .bind(toursReservationFailedQueue())
                .to(toursExchange())
                .with(TOURS_RESERVATION_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding toursReservationCancelBinding() {
        return BindingBuilder
                .bind(toursReservationCancelQueue())
                .to(toursExchange())
                .with(TOURS_RESERVATION_CANCEL_ROUTING_KEY);
    }

    @Bean
    public Binding tourExecutionStartRequestedBinding() {
        return BindingBuilder
                .bind(tourExecutionStartRequestedQueue())
                .to(toursExchange())
                .with(TOUR_EXECUTION_START_REQUESTED_ROUTING_KEY);
    }

    @Bean
    public Binding tourExecutionTokenReservedBinding() {
        return BindingBuilder
                .bind(tourExecutionTokenReservedQueue())
                .to(toursExchange())
                .with(TOUR_EXECUTION_TOKEN_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Binding tourExecutionStartFailedBinding() {
        return BindingBuilder
                .bind(tourExecutionStartFailedQueue())
                .to(toursExchange())
                .with(TOUR_EXECUTION_START_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding tourExecutionCompletedBinding() {
        return BindingBuilder
                .bind(tourExecutionCompletedQueue())
                .to(toursExchange())
                .with(TOUR_EXECUTION_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding tourExecutionAbandonedBinding() {
        return BindingBuilder
                .bind(tourExecutionAbandonedQueue())
                .to(toursExchange())
                .with(TOUR_EXECUTION_ABANDONED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}