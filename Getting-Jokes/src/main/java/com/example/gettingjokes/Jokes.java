package com.example.gettingjokes;

import java.sql.*;

public class Jokes {

    private int id;
    private String setup;
    private String delivery;

    public Jokes(int id){
        this.id = id;
        this.delivery = findJoke(id, "delivery");
        this.setup = findJoke(id, "setup");
    }

    public Jokes(int id, String setup, String delivery){
        this.delivery = delivery;
        this.setup = setup;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSetup() {
        return setup;
    }

    public String getDelivery() {
        return delivery;
    }

    public String findJoke(int id, String type) {
        Connection c = connectDB();
        Statement stmt = null;
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

}
