package com.rodrigo.lovagame;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

// Guarda los IDs de los sorteos ya enviados (uno por línea) para no repetir avisos.
public class GestorHistorial {

    private final Path archivo;

    // En GitHub Actions el historial vive en otra rama y llega por la variable
    // HISTORIAL_PATH. En local se usa historial.txt en la carpeta del proyecto.
    public GestorHistorial() {
        this(Paths.get(System.getenv().getOrDefault("HISTORIAL_PATH", "historial.txt")));
    }

    public GestorHistorial(Path archivo) {
        this.archivo = archivo;
    }

    public boolean existe() {
        return Files.exists(archivo);
    }

    // Lee el archivo y carga los IDs en memoria.
    public Set<String> cargarIdsEnviados() {
        Set<String> ids = new HashSet<>();
        if (!existe()) {
            return ids;
        }
        try {
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (!linea.isBlank()) {
                    ids.add(linea.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo historial: " + e.getMessage());
        }
        return ids;
    }

    // Añade un ID nuevo al final del archivo.
    public void guardarId(String idJuego) {
        if (idJuego == null) {
            return;
        }
        try {
            Files.writeString(archivo, idJuego + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error guardando ID: " + e.getMessage());
        }
    }
}
