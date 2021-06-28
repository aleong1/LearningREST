# LearningREST

API: https://jokeapi.dev/

## Actuator Service (Hello World):

Go to actuator-service dir

First run app with maven using: `mvnw spring-boot:run`

Open another terminal to actuator-service:

Then run localhost: `curl localhost:8080/hello-world`

With a parameter: `curl localhost:8080/hello-world?name=__`

## Jokes
Go to Getting-Jokes dir

First run app with maven using: `mvnw spring-boot:run`

In another terminal to Getting-Jokes dir:

#### To select a joke based off it's id in the database: `curl localhost:8080/select-joke?id=[id num]`

*ID has to be [1,10]*

*replace [id num] with the id number of the joke you want to select*

#### To list all jokes: `curl localhost:8080/list-jokes`

#### To add your own joke: `curl -X POST -H "Content-Type:application/json" -d "{\"id\": [id num], \"setup\": \"[first part]\", \"delivery\": \"[second part]\" }" localhost:8080/add-joke`

*replace [first part] and [second part] with the first and second parts of the joke you want to add and replace [id num] with an integer.*

*[id num] is there to follow vars the Joke class, the inputted id num will be ignored and the joke will be given an id num based off the ids already in the table*

Apparently for Windows you need a `\` before each `"` in the JSON input

#### To delete a joke based off its id: `curl -X "DELETE" localhost:8080/delete-joke?id=[id num]`

*replace [id num] with the id number of the joke you want to delete*

#### To delete all jokes in the table: `curl -X "DELETE" localhost:8080/delete-all-jokes`
