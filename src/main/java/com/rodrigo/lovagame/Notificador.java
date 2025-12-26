package com.rodrigo.lovagame;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Notificador {

    private static final String TOKEN = System.getenv("TELEGRAM_TOKEN");
    private static final String CHAT_ID = System.getenv("TELEGRAM_CHAT_ID");
    // Función para enviar mensajes
    public void enviar(String mensaje) {
        try {
         
            String mensajeSeguro = URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

            //CREAR LA URL:
            //Pegamos el Token, el ID y el mensaje para formar la dirección de envío.
            String urlString = "https://api.telegram.org/bot" + TOKEN + "/sendMessage?chat_id=" + CHAT_ID + "&text=" + mensajeSeguro;

            //ENVIAR LA PETICIÓN 
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            //ENVIAR
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            //CONFIRMAR
            if (response.statusCode() == 200) {
                System.out.println("Mensaje enviado a Telegram correctamente.");
            } else {
                System.out.println("Telegram nos ha rechazado. Código: " + response.statusCode());
                System.out.println("Respuesta: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error intentando enviar mensaje: " + e.getMessage());
        }
    }
}
