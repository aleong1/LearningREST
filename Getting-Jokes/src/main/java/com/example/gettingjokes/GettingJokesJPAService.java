package com.example.gettingjokes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GettingJokesJPAService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GettingJokesRepository repository;

    public void addJoke(Joke joke){
        joke.setId(nextId());
        repository.save(joke);
    }

    public void deleteJoke(int id){  //won't delete id 0 if it starts with 0 --> changed it to start with 1
        repository.deleteById(id);
    }

    public void deleteTuplesFromTable(){
        repository.deleteAll(); //won't delete id 0 so made first id 1
    }

    public Joke findJoke(int id){
        Optional<Joke> ret = repository.findById(id);
        return ret.get();
    }

    public List<Joke> findAllJokes(){
        return repository.findAll();
    }

    //gets the next available id in the table
    public int nextId(){
        if (repository.count() == 0){ return 1; }
        String query = "SELECT max(id) FROM jokes";
        int id = jdbcTemplate.queryForObject(query, Integer.class);
        return id + 1;
    }

}

