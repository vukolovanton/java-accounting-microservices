package com.playground.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playground.notification.config.RabbitMQConfig;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class DepositMessageHandler {

    private final JavaMailSender javaMailSender;

    @Autowired
    public DepositMessageHandler(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_DEPOSIT)
    public void receive(Message message) throws JsonProcessingException {
        byte[] body = message.getBody();
        String jsonBody = new String(body);

        ObjectMapper objectMapper = new ObjectMapper();
        DepositResponse depositResponse = objectMapper.readValue(jsonBody, DepositResponse.class);
        System.out.println(depositResponse);


        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(depositResponse.getMail());
        mailMessage.setFrom("pickle@rick.com");

        mailMessage.setSubject("Deposit");
        mailMessage.setText("Make deposit, sum: " + depositResponse.getAmount());

        try {
            javaMailSender.send(mailMessage);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}
