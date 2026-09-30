package com.rodrigo.lovagame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

// Descarga la lista de sorteos activos de la API REST de GamerPower.
public class ClienteGamerPower {

    private static final String URL_API = "https://www.gamerpower.com/api/giveaways";

    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Juego> obtenerSorteos() throws IOException, InterruptedException {
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(URL_API))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

        // Si la API falla, mejor saberlo que intentar leer una página de error como JSON.
        if (respuesta.statusCode() != 200) {
            throw new IOException("GamerPower respondió " + respuesta.statusCode());
        }
        return mapper.readValue(respuesta.body(), new TypeReference<List<Juego>>() {});
    }
}
