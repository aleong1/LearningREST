package com.example.actuatorservice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class HelloWorldController {

    private static String hello = "Hello %s!";
    private static String bye = "Bye %s!";
    private AtomicInteger counter = new AtomicInteger();
    // AtomicInteger is recommended to be used instead of int
    // in cases where there are multiple threads (connections?)
    // so the counter will be synchronized across all threads

    @GetMapping("/hello-world")
    @ResponseBody
    public Greetings sayHello(@RequestParam(name = "name", required = false, defaultValue = "World") String name){
        return new Greetings(counter.incrementAndGet(), String.format(hello, name));
    }

    @GetMapping("/bye-world")
    @ResponseBody
    public Greetings sayBye(@RequestParam(name = "name", required = false, defaultValue = "World") String name){
        return new Greetings(counter.incrementAndGet(), String.format(bye, name));
    }

}
