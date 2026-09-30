package com.rodrigo.lovagame;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificadorTest {

    private final ObjectMapper mapper = new ObjectMapper();

    // Antes el JSON se montaba a mano y un salto de línea, una barra o unas
    // comillas en el texto lo dejaban roto. Ahora tiene que salir siempre válido.
    @Test
    void elJsonDeLaFotoEsValidoAunqueElTextoTengaCaracteresRaros() throws Exception {
        String texto = "Linea 1\nLinea \"2\" con \\ barra";
        JsonNode cuerpo = mapper.readTree(
                Notificador.crearCuerpoFoto("@canal", "https://img/x.jpg", texto, "https://oferta"));

        assertEquals("@canal", cuerpo.get("chat_id").asText());
        assertEquals(texto, cuerpo.get("caption").asText());
        assertEquals("HTML", cuerpo.get("parse_mode").asText());
        assertEquals("https://oferta",
                cuerpo.get("reply_markup").get("inline_keyboard").get(0).get(0).get("url").asText());
    }

    @Test
    void elJsonDeTextoLlevaElMismoBoton() throws Exception {
        JsonNode cuerpo = mapper.readTree(Notificador.crearCuerpoTexto("@canal", "Hola", "https://oferta"));

        assertEquals("Hola", cuerpo.get("text").asText());
        assertEquals("RECLAMAR OFERTA",
                cuerpo.get("reply_markup").get("inline_keyboard").get(0).get(0).get("text").asText());
    }
}
