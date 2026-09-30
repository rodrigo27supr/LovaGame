package com.rodrigo.lovagame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// Un sorteo tal y como lo devuelve la API de GamerPower.
// Con @JsonProperty le digo a Jackson qué campo del JSON va a cada variable,
// y con ignoreUnknown ignora los campos que no me interesan.
@JsonIgnoreProperties(ignoreUnknown = true)
public class Juego {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String titulo;

    // Precio original, por ejemplo "$19.99" o "N/A".
    @JsonProperty("worth")
    private String valor;

    @JsonProperty("description")
    private String descripcion;

    // Ej: "PC, Steam" o "PC, Epic Games Store".
    @JsonProperty("platforms")
    private String plataformas;

    // "Game", "DLC", "Early Access"...
    @JsonProperty("type")
    private String tipo;

    @JsonProperty("image")
    private String imagen;

    // Fecha de fin del sorteo ("2026-10-07 23:59:00" o "N/A").
    @JsonProperty("end_date")
    private String fechaFin;

    @JsonProperty("open_giveaway_url")
    private String url;

    // Constructor vacío para Jackson.
    public Juego() {
    }

    // Constructor para crear juegos en los tests.
    public Juego(Integer id, String titulo, String valor, String descripcion, String plataformas,
                 String tipo, String imagen, String fechaFin, String url) {
        this.id = id;
        this.titulo = titulo;
        this.valor = valor;
        this.descripcion = descripcion;
        this.plataformas = plataformas;
        this.tipo = tipo;
        this.imagen = imagen;
        this.fechaFin = fechaFin;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPlataformas() {
        return plataformas;
    }

    public String getTipo() {
        return tipo;
    }

    public String getImagen() {
        return imagen;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "JUEGO: " + titulo + " | PLATAFORMA: " + plataformas + " | LINK: " + url;
    }
}
