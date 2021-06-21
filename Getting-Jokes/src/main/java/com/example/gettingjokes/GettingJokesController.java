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
    public Joke getJoke(@RequestParam(required = true, defaultValue = "0") Integer id){
        Joke j = new Joke(id);  //this way it outputs a JSON?
        return j;
    }

    @GetMapping("/list-jokes")
    @ResponseBody
    public List<Joke> listJokes(){
        List<Joke> allJokes = new ArrayList<Joke>();
        AllJokes j = new AllJokes();
        int size = j.nextId();
        for(int i = 0; i < size; i++){
            Joke tmp = new Joke(i);
            if(tmp.getSetup() != null){
                allJokes.add(new Joke(i));
            }
        }
        return allJokes;
    }

    @PostMapping("/add-joke")
    @ResponseBody
    public String insertJoke(@RequestBody Joke joke){
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
