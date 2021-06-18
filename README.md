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

To select a joke based off it's id in the database: `curl localhost:8080/select-jokes?id=__`

*ID has to be [0,9]*

To list all jokes: `curl localhost:8080/list-jokes`

*After running the app once the jokes aren't deleted since if I delete the jokes in the app there is no time to select/list a joke so they have to be manually deleted before being run again. (Any way to wait for jokes to be deleted after being done looking through the endpoints?)

To add another joke:
