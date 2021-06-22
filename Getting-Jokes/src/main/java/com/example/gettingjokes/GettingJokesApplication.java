package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class GettingJokesApplication {

    /*
    @Autowired
    private static JdbcTemplate jdbcTemplate;
    @Autowired
    private static GettingJokesService all = new GettingJokesService(jdbcTemplate);
    */

    public static void main(String[] args) {

        //GettingJokesService all = new GettingJokesService(jdbcTemplate);
        GettingJokesService all = new GettingJokesService();
        all.deleteTuplesFromTable();  //delete tuples at beginning so there are no repeated ids when inserting
        all.load();
        SpringApplication.run(GettingJokesApplication.class, args);

    }


}
