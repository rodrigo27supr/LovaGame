package com.rodrigo.lovagame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GestorHistorialTest {

    @TempDir
    Path carpeta;

    @Test
    void guardaYLeeLosIds() {
        GestorHistorial gestor = new GestorHistorial(carpeta.resolve("historial.txt"));
        assertFalse(gestor.existe());

        gestor.guardarId("10");
        gestor.guardarId("20");

        assertTrue(gestor.existe());
        assertEquals(Set.of("10", "20"), gestor.cargarIdsEnviados());
    }

    @Test
    void ignoraLineasVaciasYEspacios() throws Exception {
        Path archivo = carpeta.resolve("historial.txt");
        Files.writeString(archivo, "10\n\n 20 \r\n");

        assertEquals(Set.of("10", "20"), new GestorHistorial(archivo).cargarIdsEnviados());
    }
}
