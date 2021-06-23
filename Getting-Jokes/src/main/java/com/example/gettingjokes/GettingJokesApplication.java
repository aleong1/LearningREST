package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class GettingJokesApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GettingJokesApplication.class, args);
    }

    @Autowired
    private GettingJokesService all;

    @Override
    public void run(String... args) throws Exception {
        all.deleteTuplesFromTable();  //delete tuples at beginning so there are no repeated ids when inserting
        all.load();
    }

}
