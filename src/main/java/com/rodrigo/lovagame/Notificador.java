package com.rodrigo.lovagame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// Envía los avisos al canal de Telegram con la API HTTP del bot.
public class Notificador {

    // GitHub Secrets
    private static final String TOKEN = System.getenv("TELEGRAM_TOKEN");
    private static final String CHAT_ID = System.getenv("TELEGRAM_CHAT_ID");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public boolean estaConfigurado() {
        return TOKEN != null && !TOKEN.isBlank() && CHAT_ID != null && !CHAT_ID.isBlank();
    }

    // Envía foto + texto + botón. Si Telegram no puede usar la foto
    // (enlace roto, formato raro...), lo intenta solo con texto.
    // Devuelve true si el aviso ha llegado.
    public boolean enviarAviso(String urlImagen, String texto, String urlOferta) {
        if (!estaConfigurado()) {
            System.out.println("ERROR: Faltan las variables TELEGRAM_TOKEN o TELEGRAM_CHAT_ID.");
            return false;
        }

        if (urlImagen != null && enviar("sendPhoto", crearCuerpoFoto(CHAT_ID, urlImagen, texto, urlOferta))) {
            return true;
        }
        System.out.println("Reintentando el aviso sin imagen...");
        return enviar("sendMessage", crearCuerpoTexto(CHAT_ID, texto, urlOferta));
    }

    // Construyo el JSON con Jackson en vez de a mano: así las comillas,
    // barras y saltos de línea del texto se escapan siempre bien.
    static String crearCuerpoFoto(String chatId, String urlImagen, String texto, String urlOferta) {
        ObjectNode cuerpo = MAPPER.createObjectNode();
        cuerpo.put("chat_id", chatId);
        cuerpo.put("photo", urlImagen);
        cuerpo.put("caption", texto);
        cuerpo.put("parse_mode", "HTML");
        cuerpo.set("reply_markup", crearBoton(urlOferta));
        return cuerpo.toString();
    }

    static String crearCuerpoTexto(String chatId, String texto, String urlOferta) {
        ObjectNode cuerpo = MAPPER.createObjectNode();
        cuerpo.put("chat_id", chatId);
        cuerpo.put("text", texto);
        cuerpo.put("parse_mode", "HTML");
        cuerpo.set("reply_markup", crearBoton(urlOferta));
        return cuerpo.toString();
    }

    // Botón "RECLAMAR OFERTA" debajo del mensaje.
    private static ObjectNode crearBoton(String urlOferta) {
        ObjectNode teclado = MAPPER.createObjectNode();
        ObjectNode boton = teclado.putArray("inline_keyboard").addArray().addObject();
        boton.put("text", "RECLAMAR OFERTA");
        boton.put("url", urlOferta);
        return teclado;
    }

    private boolean enviar(String metodo, String json) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + TOKEN + "/" + metodo))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

            if (respuesta.statusCode() == 200) {
                System.out.println("Aviso enviado correctamente (" + metodo + ").");
                return true;
            }
            System.out.println("Telegram rechazó el envío (" + metodo + "). Código: " + respuesta.statusCode());
            System.out.println("Respuesta: " + respuesta.body());
            return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            System.out.println("Error enviando a Telegram (" + metodo + "): " + e.getMessage());
            return false;
        }
    }
}
