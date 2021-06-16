package com.example.gettingjokes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
        for(int i = 0; i < 10; i++){
            allJokes.add(new Jokes(i));
        }
        return allJokes;
    }

}
