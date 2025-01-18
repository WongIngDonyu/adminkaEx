package org.web.domainservice.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue createQueue() {
        return new Queue("groupCreateQueue", false);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue("groupUpdateQueue", false);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue("groupDeleteQueue", false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("groupExchange");
    }

    @Bean
    public Binding createBinding(Queue createQueue, TopicExchange exchange) {
        return BindingBuilder.bind(createQueue).to(exchange).with("groupCreate");
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, TopicExchange exchange) {
        return BindingBuilder.bind(updateQueue).to(exchange).with("groupUpdate");
    }

    @Bean
    public Binding deleteBinding(Queue deleteQueue, TopicExchange exchange) {
        return BindingBuilder.bind(deleteQueue).to(exchange).with("groupDelete");
    }
}