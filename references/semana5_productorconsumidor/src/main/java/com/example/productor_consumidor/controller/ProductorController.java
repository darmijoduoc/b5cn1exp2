package com.example.productor_consumidor.controller;

import com.example.productor_consumidor.service.ProductorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductorController {

    @Autowired
    private ProductorService producer;

    @PostMapping("/send")
    public String sendMessage(@RequestParam("message") String message) {
        producer.sendMessage(message);
        return "Mensaje enviado: " + message;
    }
}

