package com.rodrigo.lovagame;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// Construye el texto del aviso de Telegram a partir de un Juego.
// Está separado de Main para poder probarlo con tests.
public class FormateadorMensaje {

    private static final int LARGO_MAXIMO_DESCRIPCION = 100;
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String crearTexto(Juego juego) {
        StringBuilder texto = new StringBuilder();

        texto.append("<b>NUEVO REGALO DETECTADO</b>\n\n");
        texto.append("<b>").append(escaparHtml(juego.getTitulo())).append("</b>\n");
        texto.append(crearLineaValor(juego.getValor())).append("\n");
        texto.append(escaparHtml(recortarDescripcion(juego.getDescripcion()))).append("\n");

        String fechaFin = formatearFechaFin(juego.getFechaFin());
        if (fechaFin != null) {
            texto.append("<b>Termina:</b> ").append(fechaFin).append("\n");
        }

        texto.append("\n").append(crearHashtags(juego));
        return texto.toString();
    }

    // Telegram usa parse_mode HTML: si el texto trae &, < o > sin escapar, rechaza el mensaje.
    public static String escaparHtml(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    // Si conocemos el precio, lo tachamos; si no ("N/A"), solo decimos que es gratis.
    String crearLineaValor(String valor) {
        if (valor == null || valor.isBlank() || valor.equals("N/A")) {
            return "<b>GRATIS</b>";
        }
        return "<b>Valor:</b> <s>" + escaparHtml(valor) + "</s>  <b>GRATIS</b>";
    }

    // Corta la descripción sin partir palabras ni dejar saltos de línea a medias.
    String recortarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return "¡Aprovecha antes de que expire!";
        }
        String limpia = descripcion.replaceAll("\\s+", " ").trim();
        if (limpia.length() <= LARGO_MAXIMO_DESCRIPCION) {
            return limpia;
        }

        String recortada = limpia.substring(0, LARGO_MAXIMO_DESCRIPCION);
        int ultimoEspacio = recortada.lastIndexOf(' ');
        if (ultimoEspacio > 0) {
            recortada = recortada.substring(0, ultimoEspacio);
        }
        return recortada + "...";
    }

    // "2026-10-07 23:59:00" -> "07/10/2026". Devuelve null si no hay fecha.
    String formatearFechaFin(String fechaFin) {
        if (fechaFin == null || fechaFin.length() < 10) {
            return null;
        }
        try {
            return LocalDate.parse(fechaFin.substring(0, 10)).format(FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    String crearHashtags(Juego juego) {
        StringBuilder hashtags = new StringBuilder("#JuegoGratis");
        String plataformas = juego.getPlataformas();

        if (plataformas != null) {
            if (plataformas.contains("Steam")) hashtags.append(" #Steam");
            if (plataformas.contains("Epic")) hashtags.append(" #EpicGames");
            if (plataformas.contains("GOG")) hashtags.append(" #GOG");
            if (plataformas.contains("Itch")) hashtags.append(" #Itchio");
        }
        if (juego.getTipo() != null && juego.getTipo().contains("DLC")) {
            hashtags.append(" #DLC");
        }
        return hashtags.toString();
    }
}
