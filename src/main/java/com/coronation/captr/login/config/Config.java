package com.coronation.captr.login.config;

import com.coronation.captr.login.util.AppProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author toyewole
 */

@Configuration
public class Config {

    @Autowired
    AppProperties appProperties;


    @Bean
    public Queue queue() {
        return new Queue(appProperties.getNotificationQueue());
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(appProperties.getNotificationExchange());
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(appProperties.getRoutingKey());

    }

    @Bean
   public MessageConverter messageConverter () {
      return   new Jackson2JsonMessageConverter();
    }
}
