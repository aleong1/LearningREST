package com.example.gettingjokes;

public class Joke {

    private int id;
    private String setup;
    private String delivery;

    public Joke(int id){
        this.id = id;
        GettingJokesService tmp = new GettingJokesService();
        this.delivery = tmp.findJoke(id, "delivery");
        this.setup = tmp.findJoke(id, "setup");
    }

    public Joke(int id, String setup, String delivery){
        this.delivery = delivery;
        this.setup = setup;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSetup() {
        return setup;
    }

    public String getDelivery() {
        return delivery;
    }

}
