package com.example.gettingjokes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class GettingJokesController {
    //Select Joke based off its id in the database
    @GetMapping("/select-jokes")
    @ResponseBody
    public Jokes getJoke(@RequestParam(required = true, defaultValue = "0") Integer id){
        Jokes j = new Jokes(id);
        return j;
    }

    @GetMapping("/list-jokes")
    @ResponseBody
    public List<Jokes> listJokes(){
        List<Jokes> allJokes = new ArrayList<Jokes>();
        AllJokes j = new AllJokes();
        int size = j.nextId();
        for(int i = 0; i < size; i++){
            Jokes tmp = new Jokes(i);
            if(tmp.getSetup() != null){
                allJokes.add(new Jokes(i));
            }
        }
        return allJokes;
    }

    @PostMapping("/add-Joke")
    @ResponseBody
    public String insertJoke(@RequestBody NewJoke joke){
        AllJokes db = new AllJokes();
        db.addJoke(joke);
        return "Added joke";
    }

    @DeleteMapping("/delete-joke")
    @ResponseBody
    public String deleteJoke(@RequestParam(required = true) Integer id){
        AllJokes db = new AllJokes();
        db.deleteJoke(id);
        return "Deleted joke with id " + id;
    }

    @DeleteMapping("/delete-all-jokes")
    @ResponseBody
    public String deleteAllJokes(){
        AllJokes db = new AllJokes();
        db.deleteTuplesFromTable();
        return "Deleted all jokes";
    }

}
