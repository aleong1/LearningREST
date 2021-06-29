package com.example.gettingjokes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@Service
public class GettingJokesServiceJPA {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GettingJokesRepository repository;

    public void load() {
        String line;
        StringBuffer responseBack = new StringBuffer();
        //defining connection to URL
        HttpURLConnection connect = null;

        try {
            String base = "https://v2.jokeapi.dev/joke/Any?%s";
            base = String.format(base, "blacklistFlags=nsfw,religious,political,racist,sexist,explicit&%s");
            base = String.format(base, "type=twopart&%s");
            base = String.format(base, "amount=10");
            URL url = new URL(base);

            connect = (HttpURLConnection) url.openConnection();  //try with resources only works with AutoCloseable functions

            //Setting up for requests to get Jokes from URL
            connect.setRequestMethod("GET");

            //read JSON response:
            try (BufferedReader read = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {  //try with resources
                line = read.readLine();
                while (line != null) {
                    responseBack.append(line);
                    line = read.readLine();
                }
                //Make and insert Jokes to table  -- is there repository functions for this?
                makeTable();
                try {
                    insertToTable(responseBack.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch(IOException ex){
            ex.printStackTrace();
        } finally {
            connect.disconnect();
        }
    }

    //making a table in the DB
    public void makeTable(){
        String query = "CREATE TABLE IF NOT EXISTS jokes(id int primary key, setup varchar, delivery varchar)";
        jdbcTemplate.execute(query);
        System.out.println("Made Jokes table");
    }

    public void insertToTable(String data) throws JSONException {
        JSONObject jokes = new JSONObject(data);
        JSONArray listOfJokes = jokes.getJSONArray("jokes");
        for (int i = 0; i < listOfJokes.length() ; i++) {
            JSONObject joke = listOfJokes.getJSONObject(i);
            String setup = joke.getString("setup"); //PreparedStatement does not need to watch out for ' in sql
            String delivery = joke.getString("delivery");
            repository.save(new Joke(i + 1, setup, delivery));
        }
        System.out.println("Inserted tuples into table successfully");
    }

    public void addJoke(Joke joke){
        joke.setId(nextId());
        repository.save(joke);
    }

    public void deleteJoke(int id){  //won't delete id 0 if it starts with 0 --> changed it to start with 1
        repository.deleteById(id);
    }

    public void deleteTuplesFromTable(){
        repository.deleteAll(); //won't delete id 0 so made first id 1
    }

    public Joke findJoke(int id){
        Optional<Joke> ret = repository.findById(id);
        return ret.get();
    }

    public List<Joke> findAllJokes(){
        return repository.findAll();
    }

    //gets the next available id in the table
    public int nextId(){
        if (repository.count() == 0){ return 1; }
        String query = "SELECT max(id) FROM jokes";
        int id = jdbcTemplate.queryForObject(query, Integer.class);
        return id + 1;
    }

}

