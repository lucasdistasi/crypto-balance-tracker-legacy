# Crypto Balance Tracker :rocket:
Crypto Balance Tracker is a Java-Spring application that acts as a portfolio tracker for monitoring your crypto assets. 
It allows you to retrieve data such as the percentage of each coin owned, the total value of your assets, 
the current price of each coin, and the balance per platform. The application makes use of the 
[Coingecko](https://www.coingecko.com) API to fetch all the required information about the coins.

:warning: Please note that the Coingecko API has [rate limits for the Free Plan](https://www.coingecko.com/en/api/pricing). 
To avoid hitting the rate limit, a scheduler periodically retrieves the price of the saved coins every 180 seconds. 
This ensures that the end-users do not exceed the rate limit by making multiple API calls. Also, keep in mind 
that the balances displayed in the  app might not be 100% accurate due to variations in price data from different exchanges. 
However, any discrepancies should be minimal.
<br>

## Approach and Challenges
Initially, the idea was to allow users to add their wallet addresses to track their coins. However, this approach posed 
some challenges, such as specifying the network for the address and the difficulty of tracking coins held in exchange 
addresses shared by multiple users. Due to these complexities, tracking coins based on non cold/hard-wallet addresses became unfeasible. 
Instead, the current approach was adopted to provide a more reliable and feasible solution.
<br>

## IMPORTANT :fire:

---

This API uses ***Spring Security*** and ***Docker Compose***. If you want to fully test or play with it you must have
knowledge with Docker, an ADMIN user in the database and [crypto-balance-tracker-login](https://github.com/lucasdistasi/crypto-balance-tracker-login),
a Kotlin-Spring project to generate a JWT and consume the endpoints from this project.
If you don't want Authentication-Authorization make sure to disable the security in application.yml file.

<br>

#### TODO

- Migrate the app to Kotlin.

## Technologies used :sparkles:

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

### I want to try this API on my local machine. What should I do? :tada:

---

1. Have Docker installed and running.
2. Set up environment variables in _.env_ file.
3. Download mongo-seed and set your desired values.
4. If you want to secure the app, set the security.enabled property to true. Default value is false.
5. Create docker images (`docker build`) for 
   - [crypto-balance-tracker](https://github.com/lucasdistasi/crypto-balance-tracker)
   - [crypto-balance-tracker-ui](https://github.com/lucasdistasi/crypto-balance-tracker-ui)
   - [crypto-balance-tracker-login](https://github.com/lucasdistasi/crypto-balance-tracker-login) (not needed if security is disabled)
6. On crypto-balance-tracker folder run `docker compose up`

<br>

### Contributing :coffee:

Feel free to star, fork, or study from the code! If you'd like to contribute, you can gift me a coffee.

| Crypto | Network | Address                                    | QR            |
|--------|---------|--------------------------------------------|---------------|
| BTC    | Bitcoin | 15gJYCyCwoHVE3MpjwDYLM51zLRoKo2Q9h         | [BTC-bitcoin] |
| BTC    | TRC20   | TFVmahp7YQiEwd9bh4dEgF7fZyGjrQ7TRW         | [BTC-trc20]   |
| ETH    | BEP20   | 0x304714FDA2060c570B1afb1BC231C0973abBEC23 | [ETH-bep20]   |
| ETH    | ERC20   | 0x304714FDA2060c570B1afb1BC231C0973abBEC23 | [ETH-erc20]   |
| USDT   | TRC20   | TFVmahp7YQiEwd9bh4dEgF7fZyGjrQ7TRW         | [USDT-trc20]  |
| USDT   | BEP20   | 0x304714FDA2060c570B1afb1BC231C0973abBEC23 | [USDT-bep20]  |
| USDT   | ERC20   | 0x304714FDA2060c570B1afb1BC231C0973abBEC23 | [USDT-erc20]  |

[BTC-bitcoin]: https://imgur.com/Hs0DYDk
[BTC-trc20]: https://imgur.com/kdROHrE
[ETH-bep20]: https://imgur.com/DIOiJrL
[ETH-erc20]: https://imgur.com/REXkDmu
[USDT-trc20]: https://imgur.com/ubUWdpI
[USDT-bep20]: https://imgur.com/rrrYd9j
[USDT-erc20]: https://imgur.com/G9DPKvU

<br>

### Below you can find some examples with random data of the information that each endpoint retrieves :memo:

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