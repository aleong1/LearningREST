package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GettingJokesApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GettingJokesApplication.class, args);
    }

    @Autowired   //this got rid of null pointer exception
    private GettingJokesService all;

    @Override
    public void run(String... args) throws Exception {
        all.deleteTuplesFromTable();  //delete tuples at beginning so there are no repeated ids when inserting
        all.load();
    }

}
