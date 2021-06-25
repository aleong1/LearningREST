package com.example.gettingjokes;

import javax.persistence.*;

//POJO
@Entity
@Table(name = "jokes")
public class Joke {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "setup")
    private String setup;

    @Column(name = "delivery")
    private String delivery;

    public Joke(int id, String setup, String delivery){
        this.delivery = delivery;
        this.setup = setup;
        this.id = id;
    }

    public Joke() { }

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
