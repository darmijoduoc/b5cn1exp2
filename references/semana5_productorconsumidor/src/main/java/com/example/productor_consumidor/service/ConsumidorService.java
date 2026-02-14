package com.example.productor_consumidor.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumidorService {

    @RabbitListener(queues = "myQueue")
    public void receiveMessage(String message) {
        System.out.println("Mensaje Recibido: " + message);
    }
}
