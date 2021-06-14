import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Jokes {

    //defining connection
    private static HttpURLConnection connect;
    public static void main(String[] args) {
        BufferedReader read = null;
        String line;
        StringBuffer responseBack = new StringBuffer();

        try {
            String base = "https://v2.jokeapi.dev/joke/Any?%s";
            base = String.format(base, "blacklistFlags=nsfw,religious,political,racist,sexist,explicit&%s");
            base = String.format(base, "amount=10" );
            //System.out.print("URL is: " + base);
            URL url = new URL(base);
            //URL url = new URL("https://v2.jokeapi.dev/joke/Any?blacklistFlags=nsfw,religious,political,racist,sexist,explicit&amount=10");
            connect = (HttpURLConnection) url.openConnection();

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
            //read.close(); //moved to finally block
            //System.out.println(responseBack.toString());

            reading(responseBack.toString());

            //make connection to postgreSQL
            Connection c = connectDB();
            makeTable(c);
            insertToTable(c, responseBack.toString());
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
            if (type.equals("twopart")){
                String setup = joke.getString("setup");
                String delivery = joke.getString("delivery");
                System.out.println(setup + "\n\t" + delivery);
            }
            else{
                String delivery = joke.getString("joke");
                System.out.println(delivery);
            }
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
    }


    //inserting Jokes into DB table --> basically same as reading() but adding it to a table (try sep. first)
    public static void insertToTable(Connection connection, String data){
        JSONObject jokes = new JSONObject(data);
        JSONArray listOfJokes = new JSONArray(jokes.getJSONArray("jokes"));
        try {
            Statement stmt = connection.createStatement();  //try something called Prepared statement

            for (int i = 0; i < listOfJokes.length(); i++) {
                JSONObject joke = listOfJokes.getJSONObject(i);
                String type = joke.getString("type");
                String category = joke.getString("category");
                System.out.println(category + ":");
                String query;
                if (type.equals("twopart")) {
                    String setup = joke.getString("setup");
                    String delivery = joke.getString("delivery");
                    System.out.println(setup + "\n\t" + delivery);
                    setup = setup.replace("'", "''");
                    delivery = delivery.replace("'", "''");
                    query = "INSERT INTO jokes(id, setup, delivery) VALUES(" + i + ", '" + setup + "' , '" + delivery + "')";
                } else {
                    String delivery = joke.getString("joke");
                    System.out.println(delivery);
                    delivery = delivery.replace("'", "''");
                    query = "INSERT INTO jokes(id, delivery) VALUES(" + i + ", '" + delivery + "')";
                }
                System.out.print("\n");
                System.out.println("Query: " + query);
                stmt.executeUpdate(query);
            }

            System.out.println("Inserted successfully");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void deleteTuplesFromTable(Connection connection){
        Statement stmt = null;

        try {
            //making a table in the DB
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
