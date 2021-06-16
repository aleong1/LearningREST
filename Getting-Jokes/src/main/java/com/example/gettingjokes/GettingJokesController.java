package com.example.gettingjokes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class GettingJokesController {
    @GetMapping("/select-jokes")
    @ResponseBody
    public Jokes getJoke(@RequestParam(required = true, defaultValue = "0") Integer id){
        Jokes j = new Jokes(id);
        return j;
    }
}
