package com.analistas.gym.web.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook/evolution")
public class EvolutionWebhookController {

    // @PostMapping
    // public ResponseEntity<?> receive(@RequestBody Map<String, Object> payload) {

    //     System.out.println("Webhook recibido:");
    //     System.out.println(payload);

    //     return ResponseEntity.ok().build();
    // }
}
