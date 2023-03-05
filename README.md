# Crypto Balance Tracker

Crypto Balance Tracker it's a Java-Spring application that works like a portfolio tracker where you can monitor your
assets
and retrieve data like the percentage owned of each coin, the amount of money, the price of each coin, balance per
platform etc.
Crypto Balance Tracker makes use of [Coingecko](https://www.coingecko.com/) API in order to retrieve all information
about the coins.
<br>
Please bear in mind that Coingecko has a [rate limit for the Free Plan](https://www.coingecko.com/en/api/pricing).
To avoid getting a rate limit, a scheduler retrieves the price of a few saved coins every 180 seconds. By doing so, the
final user
does not call Coingecko API multiple times exceeding the rate limit.
<br>
I might add new features in the future alongside with a front-end application in order to display all the data with
charts/graphics.
<br>
<br>
My first idea was to let the user add a wallet address but I faced problems with this approach,
like specifying a network for the address. Also if you are using an exchange, your coins are in an address from the
exchange
with coins from others users, so is not possible to track just your coins. This approach might be doable only if the
address it's from a hardware-cold wallet you own.
Another issue is that services that provide an API to track balances from an address are not cheap.
So I ended up with the current approach.
<br>
<br>
Feel free to star, fork or study from the code :)

## IMPORTANT

---

This API uses ***Spring Security*** and ***Docker Compose***. If you want to test or play with it you must have
knowledge with
Docker, an ADMIN user in the database
and [crypto-balance-tracker-login](https://gitlab.com/lucas.distasi/crypto-balance-tracker-login),
a Kotlin-Spring project to generate a JWT and consume the endpoints from this project.
If you don't want Authentication-Authorization make sure to use the branch
[***no-security***](https://gitlab.com/lucas.distasi/crypto-balance-tracker/-/tree/no-security) from this project.

<br>

#### TODO

- ~~Develop a front-end application (I don't know yet if I'm going to use ReactJS, VueJS, Angular, Svelte)~~ Working on
  it
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
- Docker - Docker Compose

### I want to try this API on my local machine. What should I do?

---

- #### If you are using the no-security branch

1. You must have **MongoDB** installed in your machine.
2. Download the project.
3. Create the database. You can use a custom database name if you want.
4. Once you downloaded the project, you need to set up some environment variables.
    1. _MONGODB_DATABASE_. The database name.
    2. _MONGODB_USERNAME_. The username of your database.
    3. _MONGODB_PASSWORD_. The password of your user.
    4. _JWT_SIGNING_KEY_. The Signing Key if you are working with the secured branch.
    5. _COINGEKO_API_KEY_. The Coingeko API Key (If you have one).
5. Start the program in your favourite IDE or run `./gradlew bootRun` from the CLI.

<br>

- #### If you are using the master branch

1. Have Docker installed and running.
2. Set up environment variables in _.env_ file.
3. Download mongo-seed and set your desired values.
4. Run docker build on [crypto-balance-tracker](https://gitlab.com/lucas.distasi/crypto-balance-tracker)
   and [crypto-balance-tracker-login](https://gitlab.com/lucas.distasi/crypto-balance-tracker-login)
5. On crypto-balance-tracker folder run `docker compose up`

<br>

If you found this project useful or you learnt something from it, you can consider gifting me a coup of coffee :)

| Crypto | Network | Address                                    | QR      |
|--------|---------|--------------------------------------------|---------|
| BTC    | BEP20   | 0x03c5551d3122e9c2d6bda94521e2f75bab74de21 | [BEP20] |
| USDT   | TRC20   | TWBfjXcKcgZVajVxTZpp8qA3fyJVoEsqer         | [TRC20] |
| USDT   | BEP20   | 0x03c5551d3122e9c2d6bda94521e2f75bab74de21 | [BEP20] |

[BEP20]: https://i.imgur.com/ADeTSXC.png "BEP20"
[TRC20]: https://i.imgur.com/PbgZwew.png "TRC20"

<br>

Below you can find some random examples of the information that each endpoint retrieves.

<details>
  <summary>Response examples</summary>

### `/api/v1/crypto/balances`

#### Retrieve all coins

  ```json
{
  "total_balance": 4285.94,
  "coins": [
    {
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 22445
          },
          "total_supply": 21000000.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.11235287,
      "balance": 2521.76017,
      "percentage": 58.83,
      "platform": "TREZOR"
    },
    {
      "coin_info": {
        "id": "ethereum",
        "symbol": "eth",
        "name": "Ethereum",
        "market_data": {
          "current_price": {
            "usd": 1570.36
          },
          "total_supply": 120476407.888937,
          "max_supply": null
        }
      },
      "quantity": 1.12342812,
      "balance": 1764.18658,
      "percentage": 41.17,
      "platform": "TREZOR"
    }
  ]
}
  ```

### `/api/v1/crypto/{coinId}`

#### Retrieve all balances for the given coin

  ```json
{
  "total_balance": 409.01,
  "coins": [
    {
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 22477
          },
          "total_supply": 21000000.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.01023412,
      "balance": 230.032315,
      "percentage": 56.24,
      "platform": "TREZOR"
    },
    {
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 22477
          },
          "total_supply": 21000000.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.00796285,
      "balance": 178.98097945,
      "percentage": 43.76,
      "platform": "BINANCE"
    }
  ]
}
  ```

### `/api/v1/crypto/balances/platforms`

#### Retrieve balances for each crypto in all platforms

  ```json
{
  "totalBalance": 4349.97,
  "coinInfoResponse": [
    {
      "name": "Bitcoin",
      "quantity": 0.11321211,
      "balance": 2542.17793,
      "percentage": 58.44,
      "platforms": [
        "BINANCE",
        "TREZOR"
      ]
    },
    {
      "name": "Ethereum",
      "quantity": 1.15,
      "balance": 1807.80,
      "percentage": 41.56,
      "platforms": [
        "TREZOR"
      ]
    }
  ]
}
  ```

### `/api/v1/platform/{platform}/coins`

#### Retrieve coins and balances for the given platform

  ```json
{
  "total_balance": 169.38,
  "coins": [
    {
      "coin_info": {
        "id": "oasis-network",
        "symbol": "rose",
        "name": "Oasis Network",
        "market_data": {
          "current_price": {
            "usd": 0.056417
          },
          "total_supply": 10000000000.0,
          "max_supply": 10000000000.0
        }
      },
      "quantity": 1000,
      "balance": 56.41,
      "percentage": 33.30,
      "platform": "LEDGER"
    },
    {
      "coin_info": {
        "id": "algorand",
        "symbol": "algo",
        "name": "Algorand",
        "market_data": {
          "current_price": {
            "usd": 0.225944
          },
          "total_supply": 7340406134.54536,
          "max_supply": 10000000000.0
        }
      },
      "quantity": 500,
      "balance": 112.97,
      "percentage": 66.70,
      "platform": "LEDGER"
    }
  ]
}
  ```

### `/api/v1/platform/balances`

#### Returns total balance and percentage in each platform

  ```json
{
  "totalBalance": 2200,
  "platforms": [
    {
      "platformName": "SAFEPAL",
      "percentage": 54.54,
      "balance": 1200
    },
    {
      "platformName": "LEDGER",
      "percentage": 45.46,
      "balance": 1000
    }
  ]
}
  ```

</details>

<br>