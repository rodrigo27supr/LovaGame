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
        System.out.println("Iniciando LovaGame v1.1 (Modo PRO: Foto + Botón)...");

        Notificador notificador = new Notificador();
        GestorHistorial gestor = new GestorHistorial();
        Set<String> idsEnviados = gestor.cargarIdsEnviados();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.gamerpower.com/api/giveaways"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            List<Juego> juegos = mapper.readValue(response.body(), new TypeReference<List<Juego>>(){});

            int nuevos = 0;

            for (Juego juego : juegos) {
                String idJuego = String.valueOf(juego.id);

                if (!idsEnviados.contains(idJuego)) {
                    System.out.println("NUEVO REGALO: " + juego.title);

                    // --- ETIQUETAS ---
                    String hashtags = "#JuegoGratis";
                    if (juego.platform != null) {
                        if (juego.platform.contains("Steam")) hashtags += " #Steam";
                        else if (juego.platform.contains("Epic")) hashtags += " #EpicGames";
                        else if (juego.platform.contains("GOG")) hashtags += " #GOG";
                        else if (juego.platform.contains("Itch")) hashtags += " #Itchio";
                    }
                    if (juego.type != null && juego.type.contains("DLC")) hashtags += " #DLC";

                    // --- PRECIO ---
                    String precio = (juego.worth != null && !juego.worth.equals("N/A")) ? juego.worth : "de Pago";

                    // --- DESCRIPCIÓN  ---
                    String desc = "¡Aprovecha antes de que expire!";
                    if (juego.description != null && !juego.description.isEmpty()) {
                         desc = juego.description.length() > 100 ? juego.description.substring(0, 100) + "..." : juego.description;
                    }

                    // --- EL TEXTO ---
                    // Nota: Aquí NO ponemos el link, porque el link irá en el BOTÓN
                    String caption = "<b>NUEVO REGALO DETECTADO</b>\n\n" +
                                     "<b>" + juego.title + "</b>\n" +
                                     "<b>Valor:</b> <s>" + precio + "</s>  <b>GRATIS</b>\n" +
                                     "" + desc + "\n\n" +
                                     "" + hashtags;

                    // --- IMAGEN ---
                    // Si no tiene imagen, usamos una por defecto de GamerPower
                    String imagenParaEnviar = (juego.image != null) ? juego.image : "https://www.gamerpower.com/img/gamerpower-social-share.jpg";

                    // --- ENVIAR  ---
                    notificador.enviarFoto(imagenParaEnviar, caption, juego.url);
                    
                    // Guardar y contar
                    gestor.guardarId(idJuego);
                    nuevos++;
                    
              
                }
            }

            if (nuevos == 0) System.out.println("No hay juegos nuevos. Todo está al día.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
