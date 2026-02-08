package com.analistas.gym.model.service;

import java.util.Base64;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.analistas.gym.model.domain.Socio;

@Service
public class WhatsAppService {

    @Value("${evolution.api.url}")
    private String apiUrl;

    @Value("${evolution.api.key}")
    private String apiKey;

    @Value("${evolution.instance}")
    private String instance;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarReciboPdf(Socio socio, byte[] pdfBytes, String nombreArchivo) {

        if (socio.getTelefono() == null || socio.getTelefono().isBlank()) {
            return;
        }

        String url = apiUrl + "/message/sendMedia/" + instance;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        headers.set("apikey", apiKey);

        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        Map<String, Object> mediaMessage = Map.of(
                "mediatype", "document",
                "mimetype", "application/pdf",
                "fileName", nombreArchivo,
                "caption", "🏋️‍♂️ Captain Gym\nRecibo de pago de cuota",
                "media", base64Pdf);

        Map<String, Object> body = Map.of(
                "number", socio.getTelefono(),
                "mediaMessage", mediaMessage);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

    public void enviarMensajeTexto(String telefono, String mensaje) {

        String url = apiUrl + "/message/sendText/" + instance;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", apiKey);

        Map<String, Object> body = Map.of(
                "number", telefono,
                "textMessage", Map.of(
                        "text", mensaje));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

}
