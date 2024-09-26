package com.example.suspenso;

public class Country {
    private String name;
    private String alpha2Code;

    public Country(String name, String alpha2Code) {
        this.name = name;
        this.alpha2Code = alpha2Code;
    }

    public String getName() {
        return name;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }
}
