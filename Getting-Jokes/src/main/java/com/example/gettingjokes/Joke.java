package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;

public class Joke {

    private int id;
    private String setup;
    private String delivery;

    public Joke(int id){
        this.id = id;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setSetup(String setup) {
        this.setup = setup;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }
}
