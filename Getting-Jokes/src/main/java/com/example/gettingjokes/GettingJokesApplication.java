package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GettingJokesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GettingJokesApplication.class, args);
    }

    @Autowired   //this got rid of null pointer exception
    private GettingJokesJPAService all;


}
