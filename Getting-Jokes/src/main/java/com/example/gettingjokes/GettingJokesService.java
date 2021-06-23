package com.example.gettingjokes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.List;

@Service
public class GettingJokesService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public GettingJokesService(){}

    public void load() {
        String line;
        StringBuffer responseBack = new StringBuffer();
        //defining connection
        HttpURLConnection connect = null;

        try {
            String base = "https://v2.jokeapi.dev/joke/Any?%s";
            base = String.format(base, "blacklistFlags=nsfw,religious,political,racist,sexist,explicit&%s");
            base = String.format(base, "type=twopart&%s");
            base = String.format(base, "amount=10");
            URL url = new URL(base);

            connect = (HttpURLConnection) url.openConnection();  //try with resources only works with AutoCloseable functions

            //Setting up for requests
            connect.setRequestMethod("GET");

            //Getting a response:
            int response = connect.getResponseCode();  //response will be 200 on success

            try (BufferedReader read = new BufferedReader(new InputStreamReader(connect.getInputStream()))) {  //reads the json response
                line = read.readLine();
                while (line != null) {
                    responseBack.append(line);
                    line = read.readLine();
                }

                //make connection to postgreSQL
                //Connection c = connectDB();
                //Connection c = null;
                makeTable();
                try {
                    insertToTable(responseBack.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } //end of try with resources
        }
        catch(MalformedURLException ex){
            ex.printStackTrace();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
       // /*
        finally {
            connect.disconnect();
        }
        // */
    }

    //connecting to postgreSQL  -- won't need with jdbcTemplate
    public Connection connectDB(){
        Connection connection = null;

        try{
            //Register driver class: postgreSQL
            Class.forName("org.postgresql.Driver");

            //establishing a connection
            String user = "aleong";
            String pass = "aleong";
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", user, pass);

            //Testing connection:
            if (connection != null){
                System.out.println("Successfully connected to database");
            }
            else{
                System.out.println("Failed to connect");
            }
        }
        catch (Exception ex){
            System.out.print(ex);
        }
        return connection;
    }

    //making a table in the DB
    public void makeTable(){
        String query = "CREATE TABLE IF NOT EXISTS jokes(id int primary key, setup varchar, delivery varchar)";
        jdbcTemplate.execute(query);  //null pointer exception
        System.out.println("Made table");
    }

    public void insertToTable(String data) throws JSONException {
        JSONObject jokes = new JSONObject(data);
        JSONArray listOfJokes = jokes.getJSONArray("jokes");

        for (int i = 0; i < listOfJokes.length(); i++) {
            JSONObject joke = listOfJokes.getJSONObject(i);
            String query;

            String setup = joke.getString("setup").replace("'", "''");  //replaces ' with '' to follow SQL rules
            String delivery = joke.getString("delivery").replace("'", "''");
            //setup = setup.replace("'", "''");
            //delivery = delivery.replace("'", "''");
            query = "INSERT INTO jokes(id, setup, delivery) VALUES(?, ?, ?)";
            jdbcTemplate.update(query, i, setup, delivery);
        }

        System.out.println("Inserted successfully");

    }

    //inserting Jokes into DB table
    /*
    public void insertToTable(Connection connection, String data) throws JSONException {
        JSONObject jokes = new JSONObject(data);
        JSONArray listOfJokes = jokes.getJSONArray("jokes");
        Statement stmt = null;
        try {
            stmt = connection.createStatement();

            for (int i = 0; i < listOfJokes.length(); i++) {
                JSONObject joke = listOfJokes.getJSONObject(i);
                //String category = joke.getString("category");
                String query;

                String setup = joke.getString("setup");
                String delivery = joke.getString("delivery");
                setup = setup.replace("'", "''");
                delivery = delivery.replace("'", "''");
                query = "INSERT INTO jokes(id, setup, delivery) VALUES(" + i + ", '" + setup + "' , '" + delivery + "')";

                stmt.executeUpdate(query);
                //jdbcTemplate.update(query);  //null pointer exception
            }

            System.out.println("Inserted successfully");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        finally {
            try {
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }
    */

    //gets the next available id in the table
    public int nextId(){
        if (countJokes() == 0){
            return 0;
        }
        String query = "SELECT max(id) as maxInt FROM jokes";
        int id = jdbcTemplate.queryForObject(query, Integer.class);
        return id + 1;
    }

    public int countJokes(){
        String query = "SELECT count(*) FROM jokes";
        return jdbcTemplate.queryForObject(query, Integer.class);
    }

    //add a joke to the table
    public void addJoke(Joke joke){
        String query = "INSERT INTO jokes(id, setup, delivery) VALUES(?, ?, ?)";  //Prepared statement with jdbcTemplate
        jdbcTemplate.update(query, nextId(), joke.getSetup(), joke.getDelivery());
        System.out.println("Added Joke");
    }

    //delete Joke by id
    public void deleteJoke(int id){
        String query = "DELETE FROM jokes WHERE id = " + id;
        jdbcTemplate.execute(query);
        System.out.println("Deleted Joke");
    }

    //this deletes all tuples
    public void deleteTuplesFromTable(){
        String query = "DELETE FROM jokes";
        jdbcTemplate.execute(query);
        System.out.println("Deleted tuples from table");
    }

    //finds joke setup and delivery based off id
    public Joke findJoke(int id) {
        String query = "SELECT * FROM jokes WHERE id = ?";
        return jdbcTemplate.queryForObject(query, (rs, rowNum) ->
                new Joke(
                        rs.getInt("id"),
                        rs.getString("setup"),
                        rs.getString("delivery")
                ), new Object[]{id});
    }

    //lists all the jokes in the table
    public List<Joke> findAllJokes(){
        String query = "SELECT * FROM jokes";
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


}
