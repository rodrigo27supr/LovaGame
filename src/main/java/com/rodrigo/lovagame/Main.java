package com.rodrigo.lovagame;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static void main(String[] args) {
        System.out.println("Iniciando LovaGame con sistema de memoria...");

        //Instanciamos las clases 
        Notificador notificador = new Notificador();
        GestorHistorial gestor = new GestorHistorial();

        // Cargamos los IDs que ya hemos enviado 
        Set<String> idsEnviados = gestor.cargarIdsEnviados();
        System.out.println("Memoria cargada. Juegos ya enviados anteriormente: " + idsEnviados.size());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.gamerpower.com/api/giveaways"))
                .GET()
                .build();

        try {
            System.out.println("Buscando juegos nuevos en internet...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            List<Juego> juegos = mapper.readValue(response.body(), new TypeReference<List<Juego>>(){});

            int nuevosEncontrados = 0;

            // Recorremos los juegos y filtramos
            for (Juego juego : juegos) {
                // ID a texto para buscarlo en el historial
                String idJuego = String.valueOf(juego.id);

                // Si el ID NO está en la lista de enviados
                if (!idsEnviados.contains(idJuego)) {

                    System.out.println("NUEVO JUEGO DETECTADO -> " + juego.title);

                    String mensaje = "NUEVO GRATIS: \n" +
                            juego.title + "\n" +
                            "Valor: " + juego.worth + "\n" +
                            "Link: " + juego.url;

                    notificador.enviar(mensaje);

                    //  Meter ID al historial (lo mejoraré en un futuro)
                    gestor.guardarId(idJuego);

                    nuevosEncontrados++;

                    // Pausa para no petar Telegram
                    Thread.sleep(1000);
                }
            }

            if (nuevosEncontrados == 0) {
                System.out.println("No hay juegos nuevos. Todo está al día.");
            } else {
                System.out.println("Proceso finalizado. Se enviaron " + nuevosEncontrados + " alertas.");
            }

        } catch (Exception e) {
            System.out.println("Error en la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
