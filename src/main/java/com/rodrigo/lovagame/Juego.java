package com.rodrigo.lovagame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Juego {

    // El ID del juego
    public Integer id;

    // Lo que guardaré del juego
    public String title;
    public String worth;
    public String description;
    
    // --- NUEVAS VARIABLES ---
    
    @JsonProperty("platforms") 
    public String platform; 

    // Para saber si es DLC
    public String type;

    // Para tener el link de la foto
    public String image;

    // ------------------------------------------------

    // @JsonProperty: El traductor
    @JsonProperty("open_giveaway_url")
    public String url;

    @Override
    public String toString() {
        return "JUEGO: " + title + "\n" +
               "PLATAFORMA: " + platform + "\n" +
               "LINK: " + url + "\n" +
               "--------------------------------------------------";
    }
}
