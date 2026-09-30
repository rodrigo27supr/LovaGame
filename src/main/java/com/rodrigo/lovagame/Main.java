package com.rodrigo.lovagame;

import java.util.List;
import java.util.Set;

public class Main {

    // Pausa entre avisos para no pasar el límite de mensajes de Telegram en canales.
    private static final long PAUSA_ENTRE_AVISOS_MS = 3000;

    public static void main(String[] args) {
        System.out.println("Iniciando LovaGame v1.2...");

        Notificador notificador = new Notificador();
        GestorHistorial gestor = new GestorHistorial();
        FormateadorMensaje formateador = new FormateadorMensaje();

        // Si no hay historial (primera vez o archivo perdido), no envío nada:
        // solo apunto los sorteos actuales. Si no, mandaría 100 avisos de golpe al canal.
        boolean primeraEjecucion = !gestor.existe();
        Set<String> idsEnviados = gestor.cargarIdsEnviados();

        List<Juego> juegos;
        try {
            juegos = new ClienteGamerPower().obtenerSorteos();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } catch (Exception e) {
            System.out.println("No se pudo consultar GamerPower: " + e.getMessage());
            return;
        }

        if (primeraEjecucion) {
            System.out.println("No hay historial: guardo los " + juegos.size() + " sorteos actuales sin avisar.");
            for (Juego juego : juegos) {
                gestor.guardarId(String.valueOf(juego.getId()));
            }
            return;
        }

        if (!notificador.estaConfigurado()) {
            System.out.println("ERROR: Faltan las variables TELEGRAM_TOKEN o TELEGRAM_CHAT_ID.");
            return;
        }

        int nuevos = 0;
        int fallidos = 0;

        for (Juego juego : juegos) {
            String idJuego = String.valueOf(juego.getId());
            if (juego.getId() == null || idsEnviados.contains(idJuego)) {
                continue;
            }

            System.out.println("NUEVO REGALO: " + juego.getTitulo());

            if (nuevos + fallidos > 0) {
                esperar(PAUSA_ENTRE_AVISOS_MS);
            }

            String texto = formateador.crearTexto(juego);
            String imagen = (juego.getImagen() != null)
                    ? juego.getImagen()
                    : "https://www.gamerpower.com/img/gamerpower-social-share.jpg";

            // Solo lo apunto en el historial si el aviso ha llegado:
            // si falla, se vuelve a intentar en la siguiente ejecución.
            if (notificador.enviarAviso(imagen, texto, juego.getUrl())) {
                gestor.guardarId(idJuego);
                idsEnviados.add(idJuego);
                nuevos++;
            } else {
                fallidos++;
            }
        }

        if (nuevos == 0 && fallidos == 0) {
            System.out.println("No hay juegos nuevos. Todo está al día.");
        } else {
            System.out.println("Avisos enviados: " + nuevos + ". Pendientes para la próxima vez: " + fallidos + ".");
        }
    }

    private static void esperar(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
