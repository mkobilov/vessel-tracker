package com.vt.vtserver.config;

import com.vt.vtserver.repository.AlarmRepository;
import com.vt.vtserver.repository.StationaryObjectRepository;
import com.vt.vtserver.service.Messaging.GeoUtils;
import com.vt.vtserver.service.Messaging.Receiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableRabbit
public class RabbitMQConfiguration {
    @Autowired
    ApplicationProperties applicationProperties;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    GeoUtils geoUtils;

    public RabbitMQConfiguration() {
    }


    @Bean
    Queue queue() {
        return new Queue(applicationProperties.getQueue(), false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("spring-boot-exchange");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(applicationProperties.getQueue());
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(applicationProperties.getQueue());
        container.setMessageListener(listenerAdapter);
        container.setConcurrentConsumers(10);
        return container;
    }

    @Bean
    Receiver receiver() {
        return new Receiver(geoUtils);
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
