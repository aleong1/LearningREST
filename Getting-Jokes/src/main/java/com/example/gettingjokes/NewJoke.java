package com.example.gettingjokes;

public class NewJoke {

    //class for new jokes that someone can insert into db.
    //id is given by the db
    private String setup;
    private String delivery;

    public NewJoke(String setup, String delivery){
        this.setup = setup;
        this.delivery = delivery;
    }

    public String getSetup() {
        return setup;
    }

    public String getDelivery() {
        return delivery;
    }
}
