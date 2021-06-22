package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class GettingJokesApplication implements CommandLineRunner {

    /*
    @Autowired
    private static JdbcTemplate jdbcTemplate;
    */
    //@Autowired
    //private static GettingJokesService all = new GettingJokesService();

    public static void main(String[] args) {
        SpringApplication.run(GettingJokesApplication.class, args);
        /*
        GettingJokesService all = new GettingJokesService(jdbcTemplate);
        GettingJokesService all = new GettingJokesService();
        all.deleteTuplesFromTable();  //delete tuples at beginning so there are no repeated ids when inserting
        all.load();
         */

    }

    @Override
    public void run(String... args) throws Exception {
        GettingJokesService all = new GettingJokesService();
        all.deleteTuplesFromTable();  //delete tuples at beginning so there are no repeated ids when inserting
        all.load();
    }

}
