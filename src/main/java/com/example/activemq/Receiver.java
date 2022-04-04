package com.example.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;

@Configuration
@Component
@EnableJms
public class Receiver {


    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL));
        //core poll size=4 threads and max poll size 8 threads
        factory.setConcurrency("4-8");
        return factory;
    }

    @JmsListener(destination = "to_kts")
    public void receiveMessage(String message) {
        JmsTemplate jmsTemplate = initialize();

        jmsTemplate.send("from_kts", session -> {
            TextMessage textMessage = session.createTextMessage();
            textMessage.setText("testazo");
            return textMessage;
        });
    }

    private JmsTemplate initialize() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionTransacted(false);
        jmsTemplate.setReceiveTimeout(5000);
        jmsTemplate.setDeliveryPersistent(false);
        return jmsTemplate;
    }

}
