# Crypto Balance Tracker

Crypto Balance Tracker it's a Java-Spring application that works like a portfolio where you can track, monitor your assets and retrieve data like the percentage owned of each coin, the amount of money, the price of each coin, etc.
Crypto Balance Tracker makes use of [Coingecko](https://www.coingecko.com/) API in order to retrieve all information about the coins.
<br>
I might add new features in the future alongside with a front-end application in order to display all the data with graphics.
<br>
Feel free to star, fork or study from the code :).

#### TODO
- Develop a front-end application (I don't know yet if I'm going to use ReactJS, VueJS, Angular or Svelte)
- Secure the application with Spring Security.
- Develop a microservice to login and generate a token to be able to access this API.
- Dockerize all the microservices.
- Maybe let the microservices be ready to be deployed to the Cloud.

## Technologies used
- Java 17
- Spring 6 & Spring Boot 3
  - Spring WebFlux
  - Hibernate
  - OpenAPI
- Ehcache
- MongoDB
- Lombok
- JUnit 5 - Mockito

You can access to Swagger Docs after starting the application and going to the following URL
> http://localhost:8080/swagger-ui/index.html

Anyways, I'll add most of the docs here so you can take a look at what can you do without lookint at the code or downloading the project.

### I want to try this API on my local machine. What should I do?

---

1. You must have **MongoDB** installed in your machine.
2. Download the project.
3. Create the database. You can use a custom database name if you want.
4. Once you downloaded the project, you need to set up some environment variables.
   1. _MONGODB_DATABASE_. The database name. 
   2. _MONGODB_USERNAME_. The username of your database.
   3. _MONGODB_PASSWORD_. The password of your user.
5. Start the program in your favourite IDE or run the following command from the CLI.
>./gradlew bootRun

If you found this project useful or you learnt something from it, you can consider gifting me a coup of coffee :)

| Crypto | Network | Address                                    |
|--------|---------|--------------------------------------------|
| BTC    | BEP20   | 0x03c5551d3122e9c2d6bda94521e2f75bab74de21 |
| USDT   | TRC20   | TWBfjXcKcgZVajVxTZpp8qA3fyJVoEsqer         |
| USDT   | BEP20   | 0x03c5551d3122e9c2d6bda94521e2f75bab74de21 |
