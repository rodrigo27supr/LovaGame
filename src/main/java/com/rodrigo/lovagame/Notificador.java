package com.rodrigo.lovagame;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Notificador {

    // GitHub Secrets
    private static final String TOKEN = System.getenv("TELEGRAM_TOKEN");
    private static final String CHAT_ID = System.getenv("TELEGRAM_CHAT_ID");

    // FOTO + TEXTO + BOTN
    public void enviarFoto(String urlImagen, String textoCaption, String urlOferta) {
        try {
            if (TOKEN == null || CHAT_ID == null) {
                System.out.println("ERROR: Faltan las variables TOKEN o CHAT_ID.");
                return;
            }

            String captionSegura = textoCaption.replace("\"", "\\\"");

            // CONSTRUCCIÓN DEL JSON
            String jsonBody = String.format(
                "{" +
                    "\"chat_id\": \"%s\"," +
                    "\"photo\": \"%s\"," +
                    "\"caption\": \"%s\"," +
                    "\"parse_mode\": \"HTML\"," +
                    "\"reply_markup\": {" +
                        "\"inline_keyboard\": [[ {" +
                            "\"text\": \"RECLAMAR OFERTA \"," +
                            "\"url\": \"%s\"" +
                        "} ]]" +
                    "}" +
                "}",
                CHAT_ID, urlImagen, captionSegura, urlOferta
            );

            // PREPARAR EL ENVÍO 
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + TOKEN + "/sendPhoto")) // Endpoint de Fotos
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            // ENVIAR
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // CONFIRMAR
            if (response.statusCode() == 200) {
                System.out.println("Foto y botón enviados correctamente.");
            } else {
                System.out.println("Telegram rechazó el envío. Código: " + response.statusCode());
                System.out.println("Respuesta: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error crítico enviando foto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
