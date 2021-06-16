# LearningREST

API: https://jokeapi.dev/

## Actuator Service (Hello World):

Go to actuator-service dir

First run app with maven using: `./mvnw spring-boot:run`

Then run localhost: `curl localhost:8080/hello-world`

With a parameter: `curl localhost:8080/hello-world?name=__`

## Jokes

First run app with maven using: `./mvnw spring-boot:run`

To select a joke based off it's id in the database: `curl localhost:8080/select-jokes?id=__`
