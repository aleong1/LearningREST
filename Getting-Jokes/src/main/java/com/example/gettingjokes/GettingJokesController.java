package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GettingJokesController {

    @Autowired
    private GettingJokesService serv;

    //Select Joke based off its id in the database
    @GetMapping("/select-joke")
    @ResponseBody
    public Joke getJoke(@RequestParam(required = true, defaultValue = "0") Integer id){
        return serv.findJoke(id);
    }

    @GetMapping("/list-jokes")
    @ResponseBody
    public List<Joke> listJokes(){
        return serv.findAllJokes();
    }

    @PostMapping("/add-joke")
    @ResponseBody
    public String insertJoke(@RequestBody Joke joke){
        serv.addJoke(joke);
        return "Added joke";
    }

    @DeleteMapping("/delete-joke")
    @ResponseBody
    public String deleteJoke(@RequestParam(required = true) Integer id){
        serv.deleteJoke(id);
        return "Deleted joke with id " + id;
    }

    @DeleteMapping("/delete-all-jokes")
    @ResponseBody
    public String deleteAllJokes(){
        serv.deleteTuplesFromTable();
        return "Deleted all jokes";
    }
}
