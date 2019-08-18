package com.example.risumi.pokedex;

public class Pokedex {
    String name;
    String url;

    public Pokedex(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
