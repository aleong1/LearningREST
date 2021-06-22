package com.example.gettingjokes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

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
                Connection c = null;
                makeTable(c);
                try {
                    insertToTable(c, responseBack.toString());
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
        /*
        finally {
            connect.disconnect();
        }
         */
    }

    //connecting to postgreSQL
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

    public void makeTable(Connection connection){
        //Statement stmt = null;

        //try {
            //making a table in the DB
            String query = "CREATE TABLE IF NOT EXISTS jokes(id int primary key, setup varchar, delivery varchar)";
            /*
            stmt = connection.createStatement();

            //execute query:
            stmt.executeUpdate(query);

             */

            jdbcTemplate.execute(query);  //null pointer exception

            System.out.println("Made table");
            /*
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

             */
/*
        finally {
            try {
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

 */
    }

    //inserting Jokes into DB table
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
        ///*
        finally {
            try {
                stmt.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        // */
    }

    public int nextId(){
        Connection connection = connectDB();
        Statement stmt = null;
        int id = 0;
        try {
            //deleting all tuples from a table in the DB
            String query = "SELECT max(id) as maxInt FROM jokes";
            stmt = connection.createStatement();

            //execute query:
            ResultSet res = stmt.executeQuery(query);
            if(res.next()){
                id = res.getInt("maxInt");
                id++;
            }
            stmt.close();
            return id;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return id;
    }

    public void addJoke(Joke joke){
        Connection connection = connectDB();
        Statement stmt = null;
        try{
            String query = "INSERT INTO jokes(id, setup, delivery) VALUES(" + nextId() + ", '" + joke.getSetup() + "', '" + joke.getDelivery() + "')";
            stmt = connection.createStatement();
            System.out.println("Query: " + query);
            stmt.executeUpdate(query);
            System.out.println("Added Joke");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    //delete Joke by id
    public void deleteJoke(int id){
        Connection connection = connectDB();
        Statement stmt = null;
        try{
            String query = "DELETE FROM jokes WHERE id = " + id;
            stmt = connection.createStatement();
            stmt.executeUpdate(query);
            System.out.println("Deleted Joke");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    //this deletes all tuples
    public void deleteTuplesFromTable(){
        Connection connection = connectDB();
        Statement stmt = null;
        try {
            //deleting all tuples from a table in the DB
            String query = "DELETE FROM jokes";
            stmt = connection.createStatement();

            //execute query:
            stmt.executeUpdate(query);
            //jdbcTemplate.execute(query);
            System.out.println("Deleted tuples from table");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
       // /*
        finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        // */
    }

    public String findJoke(int id, String type) {
        GettingJokesService tmp = new GettingJokesService();
        Connection c = tmp.connectDB();
        Statement stmt;
        String ret = null;
        try{
            String query;
            if(type.equals("setup")){
                query = "SELECT setup FROM jokes WHERE id = " + id;
            }
            else{
                query = "SELECT delivery FROM jokes WHERE id = " + id;
            }

            stmt = c.createStatement();

            //execute query
            ResultSet selectedJoke = stmt.executeQuery(query);
            if(selectedJoke.next()){
                if(type.equals("setup")){
                    ret = selectedJoke.getString("setup");
                }
                else{
                    ret = selectedJoke.getString("delivery");
                }
            }
            stmt.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ret;
    }
}
