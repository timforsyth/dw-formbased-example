package uk.co.chunkybacon.dwformbased.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Fruit {

    @JsonProperty
    private final String name;
    @JsonProperty
    private final String colour;
    @JsonProperty
    private final boolean edibleSkin;

    public Fruit(String name, String colour, boolean edibleSkin) {
        this.name = name;
        this.colour = colour;
        this.edibleSkin = edibleSkin;
    }

    public String name() {
        return name;
    }
}
