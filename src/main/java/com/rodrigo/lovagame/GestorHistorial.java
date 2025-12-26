package com.rodrigo.lovagame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GestorHistorial {

    private static final String ARCHIVO = "historial.txt";

    // Método para leer el archivo y cargar los IDs en memoria
    public Set<String> cargarIdsEnviados() {
        Set<String> ids = new HashSet<>();
        Path path = Paths.get(ARCHIVO);

        // Si el archivo no existe, devolvemos la lista vacía
        if (!Files.exists(path)) {
            return ids;
        }

        try {
            List<String> lineas = Files.readAllLines(path);
            ids.addAll(lineas);
        } catch (IOException e) {
            System.out.println("Error leyendo historial: " + e.getMessage());
        }
        return ids;
    }

    // Método para guardar un ID nuevo en el archivo
    public void guardarId(String idJuego) {
        try {
            if (idJuego != null) {
                Files.writeString(Paths.get(ARCHIVO), idJuego + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.out.println("Error guardando ID: " + e.getMessage());
        }
    }
}
