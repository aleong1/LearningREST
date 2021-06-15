package com.example.actuatorservice;

public class Greetings {

    private int id;
    private String words;

    public Greetings(int id, String words){
        this.id = id;
        this.words = words;
    }

    public int getId() {
        return id;
    }

    public String getWords() {
        return words;
    }
}
