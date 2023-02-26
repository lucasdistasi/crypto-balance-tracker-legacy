# Crypto Balance Tracker

Crypto Balance Tracker it's a Java-Spring application that works like a portfolio where you can track, monitor your assets 
and retrieve data like the percentage owned of each coin, the amount of money, the price of each coin, etc.
Crypto Balance Tracker makes use of [Coingecko](https://www.coingecko.com/) API in order to retrieve all information about the coins.
<br>
Please bear in mind that Coingecko has a [rate limit for the Free Plan](https://www.coingecko.com/en/api/pricing). 
<br>
I might add new features in the future alongside with a front-end application in order to display all the data with graphics.
<br>
<br>
My first idea was to let the user add a wallet address but I faced problems some problems with this approach, 
like specifying a network for the address it' a must, also if you are using an exchange, 
your coins are in an address from the exchange with coins from others users, so is not possible to track just 
your coins. This approach might be doable only if the address it's from a hardware-cold wallet you own. 
Another issue is that services that provide an API to track balances from an address are not cheap.
So I ended up with the current approach.
<br>
<br>
Feel free to star, fork or study from the code :)

## IMPORTANT

---

This API uses ***Spring Security*** and ***Docker Compose***. If you want to test or play with it you must have knowledge with
Docker, an ADMIN user in the database and [crypto-balance-tracker-login](https://gitlab.com/lucas.distasi/crypto-balance-tracker-login), a Kotlin-Spring 
project to generate a JWT and consume the endpoints from this project.
If you don't want Authentication-Authorization make sure to use the branch ***no-security*** from this project.

<br>

#### TODO
- Develop a front-end application (I don't know yet if I'm going to use ReactJS, VueJS, Angular, Svelte or Vaadin)
- Maybe let the microservices be ready to be deployed to the Cloud.

## Technologies used
- Java 17
- Spring 6 & Spring Boot 3
  - Spring WebFlux
  - Spring Security
  - Hibernate
  - OpenAPI
- Ehcache
- MongoDB
- Lombok
- JUnit 5 - Mockito

You can access to Swagger Docs after starting the application and going to the following URL
> http://localhost:8080/swagger-ui/index.html

### What can I do with Crypto Balance Tracker?

---

- Add a Platform
- Edit a Platform
- Remove a Platform
- Retrieve all coins from the given Platform
- Update a Platform coin
- Delete a platform coin
- Add a coin
- Retrieve all coins balance
- Retrieve all balances for the given coin

### I want to try this API on my local machine. What should I do?

---

1. Read the **IMPORTANT** section above.
2. You must have **MongoDB** installed in your machine.
3. Download the project.
4. Create the database. You can use a custom database name if you want.
5. Once you downloaded the project, you need to set up some environment variables.
   1. _MONGODB_DATABASE_. The database name. 
   2. _MONGODB_USERNAME_. The username of your database.
   3. _MONGODB_PASSWORD_. The password of your user.
   4. _JWT_SIGNING_KEY_. The Signing Key if you are working with the secured branch.
   5. _COINGEKO_API_KEY_. The Coingeko API Key (If you have one).
6. Start the program in your favourite IDE or run the following command from the CLI.

>./gradlew bootRun

If you found this project useful or you learnt something from it, you can consider gifting me a coup of coffee :)

| Crypto | Network | Address                                    | QR      |
|--------|---------|--------------------------------------------|---------|
| BTC    | BEP20   | 0x03c5551d3122e9c2d6bda94521e2f75bab74de21 | [BEP20] |
| USDT   | TRC20   | TWBfjXcKcgZVajVxTZpp8qA3fyJVoEsqer         | [TRC20] |
| USDT   | BEP20   | 0x03c5551d3122e9c2d6bda94521e2f75bab74de21 | [BEP20] |

[BEP20]: https://i.imgur.com/ADeTSXC.png "BEP20"
[TRC20]: https://i.imgur.com/PbgZwew.png "TRC20"
