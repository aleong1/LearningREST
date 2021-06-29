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
public class GettingJokesServiceJDBC {

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


    // jdbc version
    //inserting Jokes into DB table
    public void insertToTable(String data) throws JSONException {
        JSONObject jokes = new JSONObject(data);
        JSONArray listOfJokes = jokes.getJSONArray("jokes");
        for (int i = 0; i < listOfJokes.length(); i++) {
            JSONObject joke = listOfJokes.getJSONObject(i);
            String setup = joke.getString("setup"); //PreparedStatement does not need to watch out for ' in sql
            String delivery = joke.getString("delivery");
            String query = "INSERT INTO jokes(id, setup, delivery) VALUES(?, ?, ?)";  //PreparedStatement
            jdbcTemplate.update(query, i + 1, setup, delivery);
        }
        System.out.println("Inserted tuples into table successfully");
    }

    //add a joke to the table
    public void addJoke(Joke joke){
        String query = "INSERT INTO jokes(id, setup, delivery) VALUES(?, ?, ?)";  //Prepared statement with jdbcTemplate
        int id = nextId();
        jdbcTemplate.update(query, id, joke.getSetup(), joke.getDelivery());
        System.out.println("Added Joke with id " + id);
    }

    //delete Joke by id
    public void deleteJoke(int id){
        String query = "DELETE FROM jokes WHERE id = " + id;
        jdbcTemplate.execute(query);
        System.out.println("Deleted Joke with id " + id);
    }

    //this deletes all tuples
    public void deleteTuplesFromTable(){
        String query = "DELETE FROM jokes";
        jdbcTemplate.execute(query);
        System.out.println("Deleted all tuples from table");
    }

    //finds joke setup and delivery based off id
    public Joke findJoke(int id) {  //queryForObject() better for selecting 1 object
        String query = "SELECT * FROM jokes WHERE id = ?";
        return jdbcTemplate.queryForObject(query, (rs, rowNum) ->
                new Joke(
                        rs.getInt("id"),
                        rs.getString("setup"),
                        rs.getString("delivery")
                ), new Object[]{id});
    }

    //lists all the jokes in the table
    public List<Joke> findAllJokes(){  //query() better for lists?
        String query = "SELECT * FROM jokes ORDER BY id";
        return jdbcTemplate.query(
                query,
                (rs, rowNum) ->
                        new Joke(
                                rs.getInt("id"),
                                rs.getString("setup"),
                                rs.getString("delivery")
                        )
        );
    }

    //gets the next available id in the table
    public int nextId(){
        if (countJokes() == 0){ return 1; }
        String query = "SELECT max(id) FROM jokes";
        int id = jdbcTemplate.queryForObject(query, Integer.class);
        return id + 1;
    }

    public int countJokes(){  //could just be repository.count()
        String query = "SELECT count(*) FROM jokes";
        return jdbcTemplate.queryForObject(query, Integer.class);
    }

}
