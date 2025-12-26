package com.rodrigo.lovagame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Juego {

    //El ID del juego
    public Integer id;

    // Lo que guardaré del juego
    public String title;       
    public String worth;       
    public String description; 
    public String platform;    

    // @JsonProperty: El traductor
    
    @JsonProperty("open_giveaway_url")
    public String url;

    // Cuando imprima, saldrá este texto.
    @Override
    public String toString() {
        return "JUEGO: " + title + "\n" +
                "VALOR: " + worth + "\n" +
                "LINK: " + url + "\n" +
                "--------------------------------------------------";
    }
}
