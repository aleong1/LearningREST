import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

public class Jokes {

    //defining connection
    HttpURLConnection connect;
    public static void main(String[] args) {
        BufferedReader read = null;
        String line;
        StringBuffer responseBack = new StringBuffer();

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
            int response = connect.getResponseCode();
            //System.out.print(response);  //Returns 200 on success

            read = new BufferedReader(new InputStreamReader(connect.getInputStream()));  //reads the json response?
            line = read.readLine();
            while(line != null){
                responseBack.append(line);
                line = read.readLine();
            }
            //reading(responseBack.toString());  //this is basically in insertToTable

            //make connection to postgreSQL
            Connection c = connectDB();
            makeTable(c);
            insertToTable(c, responseBack.toString());
            //selectJoke(1, c);
            //selectJoke(0, c);
            //deleteTuplesFromTable(c);
        }
        catch(MalformedURLException ex){
            ex.printStackTrace();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try{
                read.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            connect.disconnect();
        }
    }

    //Going through data and adding it to db
    public static void reading(String data){
        JSONObject jokes = new JSONObject(data);
        JSONArray listOfJokes = new JSONArray(jokes.getJSONArray("jokes"));
        for(int i = 0; i < listOfJokes.length(); i++){
            JSONObject joke = listOfJokes.getJSONObject(i);
            String type = joke.getString("type");
            String category = joke.getString("category");
            System.out.println(category + ":");

            String setup = joke.getString("setup");
            String delivery = joke.getString("delivery");
            System.out.println(setup + "\n\t" + delivery);
            System.out.print("\n");
        }
    }

    //connecting to postgreSQL
    public static Connection connectDB(){
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
                System.out.println("Successfully connected");
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

    public static void makeTable(Connection connection){
        Statement stmt = null;

        try {
            //making a table in the DB
            String query = "CREATE TABLE IF NOT EXISTS jokes(id int primary key, setup varchar(200), delivery varchar(200))";
            stmt = connection.createStatement();

            //execute query:
            stmt.executeUpdate(query);

            System.out.println("Made table");
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


    //inserting Jokes into DB table --> basically same as reading() but adding it to a table (try sep. first)
    public static void insertToTable(Connection connection, String data) {
        JSONObject jokes = new JSONObject(data);
        JSONArray listOfJokes = new JSONArray(jokes.getJSONArray("jokes"));
        Statement stmt = null;
        try {
            stmt = connection.createStatement();

            for (int i = 0; i < listOfJokes.length(); i++) {
                JSONObject joke = listOfJokes.getJSONObject(i);
                String type = joke.getString("type");
                String category = joke.getString("category");
                String query;

                String setup = joke.getString("setup");
                String delivery = joke.getString("delivery");
                setup = setup.replace("'", "''");
                delivery = delivery.replace("'", "''");
                query = "INSERT INTO jokes(id, setup, delivery) VALUES(" + i + ", '" + setup + "' , '" + delivery + "')";

                stmt.executeUpdate(query);
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

    public static void selectJoke(int id, Connection connection){
        Statement stmt = null;
        try{
            String query = "SELECT * FROM jokes WHERE id = " + id;
            stmt = connection.createStatement();

            //execute query
            ResultSet selectedJoke = stmt.executeQuery(query);
            if(selectedJoke.next()){
                String setup = selectedJoke.getString("setup");
                String delivery = selectedJoke.getString("delivery");
                System.out.println("ID: " + id + "\n" + setup + "\n\t" + delivery);
            }
            stmt.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void deleteTuplesFromTable(Connection connection){
        Statement stmt = null;
        try {
            //deleting all tuples from a table in the DB
            String query = "DELETE FROM jokes";
            stmt = connection.createStatement();

            //execute query:
            stmt.executeUpdate(query);
            System.out.println("Deleted tuples from table");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
