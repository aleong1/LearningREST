package com.example.gettingjokes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testforListandAddJokes() {
        Joke a = new Joke(1, "What is a dying programmer's last program?", "Goodbye, world!");
        Joke b = new Joke(1, "I hate Russian matryoshka dolls.", "They're so full of themselves.");
        Joke c = new Joke(1, "What does a turkey dress up as for Halloween?", "A gobblin'!" );
        Joke d = new Joke(2, "Why are cats so good at video games?","They have nine lives." );
        Joke e = new Joke(4, "Why do ghosts go on diets?", "So they can keep their ghoulish figures.");

        restTemplate.postForEntity("http://localhost:" + port + "/add-joke", a, String.class);

        ResponseEntity<Joke[]> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/list-jokes", Joke[].class);
        Joke[] jokes = responseEntity.getBody();
        System.out.print(jokes);
        assert (jokes.length == 1);
        assert (jokes[0].getSetup().equals("What is a dying programmer's last program?"));
        assert (jokes[0].getDelivery().equals("Goodbye, world!"));
        assert (jokes[0].getId() == 1);

        restTemplate.postForEntity("http://localhost:" + port + "/add-joke", b, String.class);
        restTemplate.postForEntity("http://localhost:" + port + "/add-joke", c, String.class);
        restTemplate.postForEntity("http://localhost:" + port + "/add-joke", d, String.class);
        restTemplate.postForEntity("http://localhost:" + port + "/add-joke", e, String.class);

        responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/list-jokes", Joke[].class);
        jokes = responseEntity.getBody();
        assert (jokes.length == 5);
        assert (jokes[3].getSetup().equals("Why are cats so good at video games?"));
        assert (jokes[1].getDelivery().equals("They're so full of themselves."));
        assert (jokes[4].getId() == 5);
        assert (jokes[1].getId() == 2);
    }

    @Test
    public void testforSelectJokes(){
        String selectURL = "http://localhost:" + port + "/select-joke?id=";
        Joke selected = restTemplate.getForObject( selectURL + "1", Joke.class);
        assert (selected.getId() == 1);
        assert (selected.getSetup().equals("What is a dying programmer's last program?"));
        assert (selected.getDelivery().equals("Goodbye, world!"));
    }


}
