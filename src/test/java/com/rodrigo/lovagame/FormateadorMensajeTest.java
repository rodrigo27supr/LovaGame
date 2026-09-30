package com.rodrigo.lovagame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormateadorMensajeTest {

    private final FormateadorMensaje formateador = new FormateadorMensaje();

    private Juego juego(String titulo, String valor, String descripcion, String plataformas, String tipo, String fechaFin) {
        return new Juego(1, titulo, valor, descripcion, plataformas, tipo, null, fechaFin, "https://ejemplo.com");
    }

    @Test
    void escapaLosCaracteresQueRompenElHtmlDeTelegram() {
        String texto = formateador.crearTexto(juego("Tom & Jerry <Edición>", "$5.99",
                "Consigue gratis Tom & Jerry", "PC, Steam", "Game", "N/A"));

        assertTrue(texto.contains("<b>Tom &amp; Jerry &lt;Edición&gt;</b>"));
        assertTrue(texto.contains("Consigue gratis Tom &amp; Jerry"));
        assertFalse(texto.contains("Tom & Jerry"));
    }

    @Test
    void tachaElPrecioSoloSiLoConocemos() {
        assertEquals("<b>Valor:</b> <s>$19.99</s>  <b>GRATIS</b>", formateador.crearLineaValor("$19.99"));
        assertEquals("<b>GRATIS</b>", formateador.crearLineaValor("N/A"));
        assertEquals("<b>GRATIS</b>", formateador.crearLineaValor(null));
    }

    @Test
    void recortaLaDescripcionSinPartirPalabras() {
        String larga = "Palabra ".repeat(30);
        String recortada = formateador.recortarDescripcion(larga);

        assertTrue(recortada.endsWith("Palabra..."));
        assertTrue(recortada.length() <= 103);
    }

    @Test
    void quitaLosSaltosDeLineaDeLaDescripcion() {
        assertEquals("Linea uno Linea dos", formateador.recortarDescripcion("Linea uno\n\nLinea dos"));
    }

    @Test
    void usaUnTextoPorDefectoSiNoHayDescripcion() {
        assertEquals("¡Aprovecha antes de que expire!", formateador.recortarDescripcion(""));
        assertEquals("¡Aprovecha antes de que expire!", formateador.recortarDescripcion(null));
    }

    @Test
    void muestraLaFechaDeFinSiExiste() {
        assertEquals("07/10/2026", formateador.formatearFechaFin("2026-10-07 23:59:00"));
        assertNull(formateador.formatearFechaFin("N/A"));
        assertNull(formateador.formatearFechaFin(null));

        String texto = formateador.crearTexto(juego("Juego", "N/A", "", "PC", "Game", "2026-10-07 23:59:00"));
        assertTrue(texto.contains("<b>Termina:</b> 07/10/2026"));
    }

    @Test
    void creaLosHashtagsSegunPlataformaYTipo() {
        assertEquals("#JuegoGratis #Steam #DLC",
                formateador.crearHashtags(juego("X", null, null, "PC, Steam", "DLC", null)));
        assertEquals("#JuegoGratis #EpicGames",
                formateador.crearHashtags(juego("X", null, null, "PC, Epic Games Store", "Game", null)));
        assertEquals("#JuegoGratis",
                formateador.crearHashtags(juego("X", null, null, null, null, null)));
    }
}
