package com.example.gettingjokes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GettingJokesApplication {

    public static void main(String[] args) {

        AllJokes all = new AllJokes();
        SpringApplication.run(GettingJokesApplication.class, args);
        //all.deleteTuplesFromTable();
    }

}