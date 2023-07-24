# Crypto Balance Tracker :rocket:

![Pipeline Status](https://github.com/lucasdistasi/crypto-balance-tracker/actions/workflows/main.yml/badge.svg)
[![HitCount](https://hits.dwyl.com/lucasdistasi/crypto-balance-tracker.svg)](https://hits.dwyl.com/lucasdistasi/crypto-balance-tracker)

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
This API uses ***Spring Security*** and ***Docker Compose***. If you want to fully test or play with it you must have
knowledge with Docker, an ADMIN user in the database and [crypto-balance-tracker-login](https://github.com/lucasdistasi/crypto-balance-tracker-login),
a Kotlin-Spring project to generate a JWT and consume the endpoints from this project.
If you don't want Authentication-Authorization make sure to disable the security in application.yml file.

<br>

#### TODO

- Maybe migrate the app to Kotlin or Java 21 (when available).

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

Before starting, you must know that this application can be used with security, so by enabling security, all endpoints will
require a JWT token from an user with ADMIN role. That being said, below you can find the instructions to run the application
with or without security.

1. Have Docker installed and running.
2. Set up environment variables in _.env_ file.
   1. MONGODB_DATABASE. The name of the database.
   2. JWT_SIGNING_KEY. The signing key. Leave empty if security is disabled.
   3. COINGEKO_API_KEY. API Key from PRO Account. If you don't have one, leave it empty.
3. Download [cbt-mongo-seed](https://github.com/lucasdistasi/cbt-mongo.seed) and set your desired values (not needed if security is disabled).
4. If you want to secure the app, set the security.enabled property in application.yml from this project to true. Default value is false.
5. Run `./gradlew bootJar` on the root of this project to create the executable jar that's going to be used by Docker.
6. Create docker images (`docker build`) for 
   - [cbt-mongo-seed](https://github.com/lucasdistasi/cbt-mongo.seed) (not needed if security is disabled)
   - [crypto-balance-tracker](https://github.com/lucasdistasi/crypto-balance-tracker)
   - [crypto-balance-tracker-ui](https://github.com/lucasdistasi/crypto-balance-tracker-ui)
   - [crypto-balance-tracker-login](https://github.com/lucasdistasi/crypto-balance-tracker-login) (not needed if security is disabled)
7. On crypto-balance-tracker folder run `docker-compose -f docker-compose-no-security.yml up` if you don't want to use it with security or `docker compose up` if you want to use it with security.

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

Bear in mind that the below ones aren't all the endpoints, but only the ones used to display data in tables and charts, aka GET endpoints.

<details>
  <summary>Response examples</summary>

### `/api/v1/cryptos?page=0`

#### Retrieve cryptos by page

```json
{
  "page": 1,
  "totalPages": 1,
  "hasNextPage": false,
  "cryptos": [
    {
      "coinId": "64bd318372a86834e9b400b1",
      "coinName": "Bitcoin",
      "platform": "COINBASE",
      "quantity": 0.1
    },
    {
      "coinId": "64bd319172a86834e9b400b2",
      "coinName": "Ethereum",
      "platform": "COINBASE",
      "quantity": 0.5
    },
    {
      "coinId": "64bd319b72a86834e9b400b3",
      "coinName": "Cardano",
      "platform": "BINANCE",
      "quantity": 500
    },
    {
      "coinId": "64bd31ad72a86834e9b400b4",
      "coinName": "Tether",
      "platform": "OKX",
      "quantity": 750
    },
    {
      "coinId": "64bd31c772a86834e9b400b5",
      "coinName": "XRP",
      "platform": "BYBIT",
      "quantity": 500
    },
    {
      "coinId": "64bd31e072a86834e9b400b6",
      "coinName": "Solana",
      "platform": "KRAKEN",
      "quantity": 30
    },
    {
      "coinId": "64bd31eb72a86834e9b400b7",
      "coinName": "Polygon",
      "platform": "KRAKEN",
      "quantity": 100
    },
    {
      "coinId": "64bd322572a86834e9b400b8",
      "coinName": "Bitcoin",
      "platform": "BINANCE",
      "quantity": 0.015
    }
  ]
}
```

### `/api/v1/crypto/{id}`

#### Retrieves information from the crypto with the given mongo database id

```json
{
  "coinId": "64bd318372a86834e9b400b1",
  "coinName": "Bitcoin",
  "platform": "COINBASE",
  "quantity": 0.1
}
```

### `/api/v1/platforms`

#### Retrieves all platforms

```json
[
  {
    "name": "BINANCE"
  },
  {
    "name": "COINBASE"
  },
  {
    "name": "BYBIT"
  },
  {
    "name": "OKX"
  },
  {
    "name": "KRAKEN"
  }
]
```

### `/api/v1/goals`

#### Retrieves all goals

```json
[
  {
    "goalId": "64bd326072a86834e9b400bb",
    "cryptoName": "XRP",
    "actualQuantity": 500,
    "progress": 100,
    "remainingQuantity": 0,
    "goalQuantity": 500,
    "moneyNeeded": 0
  },
  {
    "goalId": "64bd324472a86834e9b400ba",
    "cryptoName": "Ethereum",
    "actualQuantity": 0.5,
    "progress": 50.00,
    "remainingQuantity": 0.5,
    "goalQuantity": 1,
    "moneyNeeded": 937.04
  },
  {
    "goalId": "64bd323972a86834e9b400b9",
    "cryptoName": "Bitcoin",
    "actualQuantity": 0.115,
    "progress": 23.00,
    "remainingQuantity": 0.385,
    "goalQuantity": 0.5,
    "moneyNeeded": 11510.73
  }
]
```

### `/api/v1/dashboards/crypto/balances`

#### Returns total balances and information of each crypto in each platform

```json
{
  "totalBalance": 6464.53,
  "totalEURBalance": 5808.95,
  "totalBTCBalance": 0.2162795750,
  "coins": [
    {
      "coin_id": "64bd318372a86834e9b400b1",
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 29889,
            "eur": 26858,
            "btc": 1.0
          },
          "circulating_supply": 19437025.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.1,
      "balance": 2988.90,
      "balance_in_eur": 2685.80,
      "balance_in_btc": 0.1000000000,
      "percentage": 46.24,
      "platform": "COINBASE"
    },
    {
      "coin_id": "64bd319172a86834e9b400b2",
      "coin_info": {
        "id": "ethereum",
        "symbol": "eth",
        "name": "Ethereum",
        "market_data": {
          "current_price": {
            "usd": 1873.98,
            "eur": 1683.94,
            "btc": 0.06269515
          },
          "circulating_supply": 120194007.919753,
          "max_supply": null
        }
      },
      "quantity": 0.5,
      "balance": 936.99,
      "balance_in_eur": 841.97,
      "balance_in_btc": 0.0313475750,
      "percentage": 14.49,
      "platform": "COINBASE"
    },
    {
      "coin_id": "64bd31ad72a86834e9b400b4",
      "coin_info": {
        "id": "tether",
        "symbol": "usdt",
        "name": "Tether",
        "market_data": {
          "current_price": {
            "usd": 1.0,
            "eur": 0.898737,
            "btc": 0.00003346
          },
          "circulating_supply": 83796187894.4596,
          "max_supply": null
        }
      },
      "quantity": 750,
      "balance": 750.00,
      "balance_in_eur": 674.05,
      "balance_in_btc": 0.0250950000,
      "percentage": 11.60,
      "platform": "OKX"
    },
    {
      "coin_id": "64bd31e072a86834e9b400b6",
      "coin_info": {
        "id": "solana",
        "symbol": "sol",
        "name": "Solana",
        "market_data": {
          "current_price": {
            "usd": 24.61,
            "eur": 22.11,
            "btc": 0.0008232
          },
          "circulating_supply": 404138972.522103,
          "max_supply": null
        }
      },
      "quantity": 30,
      "balance": 738.30,
      "balance_in_eur": 663.30,
      "balance_in_btc": 0.0246960000,
      "percentage": 11.42,
      "platform": "KRAKEN"
    },
    {
      "coin_id": "64bd322572a86834e9b400b8",
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 29889,
            "eur": 26858,
            "btc": 1.0
          },
          "circulating_supply": 19437025.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.015,
      "balance": 448.34,
      "balance_in_eur": 402.87,
      "balance_in_btc": 0.0150000000,
      "percentage": 6.94,
      "platform": "BINANCE"
    },
    {
      "coin_id": "64bd31c772a86834e9b400b5",
      "coin_info": {
        "id": "ripple",
        "symbol": "xrp",
        "name": "XRP",
        "market_data": {
          "current_price": {
            "usd": 0.739053,
            "eur": 0.664109,
            "btc": 0.00002473
          },
          "circulating_supply": 52544091958.0,
          "max_supply": 100000000000.0
        }
      },
      "quantity": 500,
      "balance": 369.53,
      "balance_in_eur": 332.05,
      "balance_in_btc": 0.0123650000,
      "percentage": 5.72,
      "platform": "BYBIT"
    },
    {
      "coin_id": "64bd319b72a86834e9b400b3",
      "coin_info": {
        "id": "cardano",
        "symbol": "ada",
        "name": "Cardano",
        "market_data": {
          "current_price": {
            "usd": 0.315106,
            "eur": 0.283152,
            "btc": 0.00001054
          },
          "circulating_supply": 35045020830.3234,
          "max_supply": 45000000000.0
        }
      },
      "quantity": 500,
      "balance": 157.55,
      "balance_in_eur": 141.58,
      "balance_in_btc": 0.0052700000,
      "percentage": 2.44,
      "platform": "BINANCE"
    },
    {
      "coin_id": "64bd31eb72a86834e9b400b7",
      "coin_info": {
        "id": "matic-network",
        "symbol": "matic",
        "name": "Polygon",
        "market_data": {
          "current_price": {
            "usd": 0.749241,
            "eur": 0.673264,
            "btc": 0.00002506
          },
          "circulating_supply": 9319469069.28493,
          "max_supply": 10000000000.0
        }
      },
      "quantity": 100,
      "balance": 74.92,
      "balance_in_eur": 67.33,
      "balance_in_btc": 0.0025060000,
      "percentage": 1.16,
      "platform": "KRAKEN"
    }
  ]
}
```

### `/api/v1/dashboards/crypto/{coinId}`

#### Retrieves information from the crypto with the given coinId

```json
{
  "totalBalance": 3437.24,
  "totalEURBalance": 3088.67,
  "totalBTCBalance": 0.1150000000,
  "coins": [
    {
      "coin_id": "64bd318372a86834e9b400b1",
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 29889,
            "eur": 26858,
            "btc": 1.0
          },
          "circulating_supply": 19437025.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.1,
      "balance": 2988.90,
      "balance_in_eur": 2685.80,
      "balance_in_btc": 0.1000000000,
      "percentage": 86.96,
      "platform": "COINBASE"
    },
    {
      "coin_id": "64bd322572a86834e9b400b8",
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 29889,
            "eur": 26858,
            "btc": 1.0
          },
          "circulating_supply": 19437025.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.015,
      "balance": 448.34,
      "balance_in_eur": 402.87,
      "balance_in_btc": 0.0150000000,
      "percentage": 13.04,
      "platform": "BINANCE"
    }
  ]
}
```

### `/api/v1/dashboards/cryptos`

#### Retrieves information for each crypto and the percentage distribution in each platform for that crypto

```json
[
  {
    "crypto": "ethereum",
    "coins": [
      {
        "coin_id": "64bd319172a86834e9b400b2",
        "coin_info": {
          "id": "ethereum",
          "symbol": "eth",
          "name": "Ethereum",
          "market_data": {
            "current_price": {
              "usd": 1873.98,
              "eur": 1683.94,
              "btc": 0.06269515
            },
            "circulating_supply": 120194007.919753,
            "max_supply": null
          }
        },
        "quantity": 0.5,
        "balance": 936.99,
        "balance_in_eur": 841.97,
        "balance_in_btc": 0.0313475750,
        "percentage": 100.00,
        "platform": "COINBASE"
      }
    ]
  },
  {
    "crypto": "ripple",
    "coins": [
      {
        "coin_id": "64bd31c772a86834e9b400b5",
        "coin_info": {
          "id": "ripple",
          "symbol": "xrp",
          "name": "XRP",
          "market_data": {
            "current_price": {
              "usd": 0.739053,
              "eur": 0.664109,
              "btc": 0.00002473
            },
            "circulating_supply": 52544091958.0,
            "max_supply": 100000000000.0
          }
        },
        "quantity": 500,
        "balance": 369.53,
        "balance_in_eur": 332.05,
        "balance_in_btc": 0.0123650000,
        "percentage": 100.00,
        "platform": "BYBIT"
      }
    ]
  },
  {
    "crypto": "tether",
    "coins": [
      {
        "coin_id": "64bd31ad72a86834e9b400b4",
        "coin_info": {
          "id": "tether",
          "symbol": "usdt",
          "name": "Tether",
          "market_data": {
            "current_price": {
              "usd": 1.0,
              "eur": 0.898737,
              "btc": 0.00003346
            },
            "circulating_supply": 83796187894.4596,
            "max_supply": null
          }
        },
        "quantity": 750,
        "balance": 750.00,
        "balance_in_eur": 674.05,
        "balance_in_btc": 0.0250950000,
        "percentage": 100.00,
        "platform": "OKX"
      }
    ]
  },
  {
    "crypto": "cardano",
    "coins": [
      {
        "coin_id": "64bd319b72a86834e9b400b3",
        "coin_info": {
          "id": "cardano",
          "symbol": "ada",
          "name": "Cardano",
          "market_data": {
            "current_price": {
              "usd": 0.315106,
              "eur": 0.283152,
              "btc": 0.00001054
            },
            "circulating_supply": 35045020830.3234,
            "max_supply": 45000000000.0
          }
        },
        "quantity": 500,
        "balance": 157.55,
        "balance_in_eur": 141.58,
        "balance_in_btc": 0.0052700000,
        "percentage": 100.00,
        "platform": "BINANCE"
      }
    ]
  },
  {
    "crypto": "solana",
    "coins": [
      {
        "coin_id": "64bd31e072a86834e9b400b6",
        "coin_info": {
          "id": "solana",
          "symbol": "sol",
          "name": "Solana",
          "market_data": {
            "current_price": {
              "usd": 24.61,
              "eur": 22.11,
              "btc": 0.0008232
            },
            "circulating_supply": 404138972.522103,
            "max_supply": null
          }
        },
        "quantity": 30,
        "balance": 738.30,
        "balance_in_eur": 663.30,
        "balance_in_btc": 0.0246960000,
        "percentage": 100.00,
        "platform": "KRAKEN"
      }
    ]
  },
  {
    "crypto": "matic-network",
    "coins": [
      {
        "coin_id": "64bd31eb72a86834e9b400b7",
        "coin_info": {
          "id": "matic-network",
          "symbol": "matic",
          "name": "Polygon",
          "market_data": {
            "current_price": {
              "usd": 0.749606,
              "eur": 0.673591,
              "btc": 0.00002507
            },
            "circulating_supply": 9319469069.28493,
            "max_supply": 10000000000.0
          }
        },
        "quantity": 100,
        "balance": 74.96,
        "balance_in_eur": 67.36,
        "balance_in_btc": 0.0025070000,
        "percentage": 100.00,
        "platform": "KRAKEN"
      }
    ]
  },
  {
    "crypto": "bitcoin",
    "coins": [
      {
        "coin_id": "64bd318372a86834e9b400b1",
        "coin_info": {
          "id": "bitcoin",
          "symbol": "btc",
          "name": "Bitcoin",
          "market_data": {
            "current_price": {
              "usd": 29889,
              "eur": 26858,
              "btc": 1.0
            },
            "circulating_supply": 19437025.0,
            "max_supply": 21000000.0
          }
        },
        "quantity": 0.1,
        "balance": 2988.90,
        "balance_in_eur": 2685.80,
        "balance_in_btc": 0.1000000000,
        "percentage": 86.96,
        "platform": "COINBASE"
      },
      {
        "coin_id": "64bd322572a86834e9b400b8",
        "coin_info": {
          "id": "bitcoin",
          "symbol": "btc",
          "name": "Bitcoin",
          "market_data": {
            "current_price": {
              "usd": 29889,
              "eur": 26858,
              "btc": 1.0
            },
            "circulating_supply": 19437025.0,
            "max_supply": 21000000.0
          }
        },
        "quantity": 0.015,
        "balance": 448.34,
        "balance_in_eur": 402.87,
        "balance_in_btc": 0.0150000000,
        "percentage": 13.04,
        "platform": "BINANCE"
      }
    ]
  }
]
```

### `/api/v1/dashboards/crypto/balances/platforms`

#### Retrieves information for each crypto adding the individual values from each platform where that crypto is stored

```json
{
  "totalBalance": 6466.29,
  "coinInfoResponse": [
    {
      "name": "Bitcoin",
      "quantity": 0.115,
      "balance": 3438.04,
      "percentage": 53.17,
      "platforms": [
        "BINANCE",
        "COINBASE"
      ]
    },
    {
      "name": "Ethereum",
      "quantity": 0.5,
      "balance": 937.26,
      "percentage": 14.49,
      "platforms": [
        "COINBASE"
      ]
    },
    {
      "name": "Tether",
      "quantity": 750,
      "balance": 749.96,
      "percentage": 11.60,
      "platforms": [
        "OKX"
      ]
    },
    {
      "name": "Solana",
      "quantity": 30,
      "balance": 739.20,
      "percentage": 11.43,
      "platforms": [
        "KRAKEN"
      ]
    },
    {
      "name": "XRP",
      "quantity": 500,
      "balance": 369.56,
      "percentage": 5.72,
      "platforms": [
        "BYBIT"
      ]
    },
    {
      "name": "Cardano",
      "quantity": 500,
      "balance": 157.31,
      "percentage": 2.43,
      "platforms": [
        "BINANCE"
      ]
    },
    {
      "name": "Polygon",
      "quantity": 100,
      "balance": 74.96,
      "percentage": 1.16,
      "platforms": [
        "KRAKEN"
      ]
    }
  ]
}
```

### `/api/v1/dashboards/platform/{platformName}/coins`

#### Retrieves information for all cryptos stored in the given platform

```json
{
  "totalBalance": 605.75,
  "totalEURBalance": 544.32,
  "totalBTCBalance": 0.0202600000,
  "coins": [
    {
      "coin_id": "64bd322572a86834e9b400b8",
      "coin_info": {
        "id": "bitcoin",
        "symbol": "btc",
        "name": "Bitcoin",
        "market_data": {
          "current_price": {
            "usd": 29896,
            "eur": 26864,
            "btc": 1.0
          },
          "circulating_supply": 19437025.0,
          "max_supply": 21000000.0
        }
      },
      "quantity": 0.015,
      "balance": 448.44,
      "balance_in_eur": 402.96,
      "balance_in_btc": 0.0150000000,
      "percentage": 74.03,
      "platform": "BINANCE"
    },
    {
      "coin_id": "64bd319b72a86834e9b400b3",
      "coin_info": {
        "id": "cardano",
        "symbol": "ada",
        "name": "Cardano",
        "market_data": {
          "current_price": {
            "usd": 0.314627,
            "eur": 0.282722,
            "btc": 0.00001052
          },
          "circulating_supply": 35045020830.3234,
          "max_supply": 45000000000.0
        }
      },
      "quantity": 500,
      "balance": 157.31,
      "balance_in_eur": 141.36,
      "balance_in_btc": 0.0052600000,
      "percentage": 25.97,
      "platform": "BINANCE"
    }
  ]
}
```

### `/api/v1/dashboards/platforms/coins`

#### Retrieves information for all cryptos stored in each platform

```json
[
  {
    "platform": "BINANCE",
    "coins": [
      {
        "coin_id": "64bd322572a86834e9b400b8",
        "coin_info": {
          "id": "bitcoin",
          "symbol": "btc",
          "name": "Bitcoin",
          "market_data": {
            "current_price": {
              "usd": 29896,
              "eur": 26864,
              "btc": 1.0
            },
            "circulating_supply": 19437025.0,
            "max_supply": 21000000.0
          }
        },
        "quantity": 0.015,
        "balance": 448.44,
        "balance_in_eur": 402.96,
        "balance_in_btc": 0.0150000000,
        "percentage": 74.03,
        "platform": "BINANCE"
      },
      {
        "coin_id": "64bd319b72a86834e9b400b3",
        "coin_info": {
          "id": "cardano",
          "symbol": "ada",
          "name": "Cardano",
          "market_data": {
            "current_price": {
              "usd": 0.314627,
              "eur": 0.282722,
              "btc": 0.00001052
            },
            "circulating_supply": 35045020830.3234,
            "max_supply": 45000000000.0
          }
        },
        "quantity": 500,
        "balance": 157.31,
        "balance_in_eur": 141.36,
        "balance_in_btc": 0.0052600000,
        "percentage": 25.97,
        "platform": "BINANCE"
      }
    ]
  },
  {
    "platform": "COINBASE",
    "coins": [
      {
        "coin_id": "64bd318372a86834e9b400b1",
        "coin_info": {
          "id": "bitcoin",
          "symbol": "btc",
          "name": "Bitcoin",
          "market_data": {
            "current_price": {
              "usd": 29896,
              "eur": 26864,
              "btc": 1.0
            },
            "circulating_supply": 19437025.0,
            "max_supply": 21000000.0
          }
        },
        "quantity": 0.1,
        "balance": 2989.60,
        "balance_in_eur": 2686.40,
        "balance_in_btc": 0.1000000000,
        "percentage": 76.13,
        "platform": "COINBASE"
      },
      {
        "coin_id": "64bd319172a86834e9b400b2",
        "coin_info": {
          "id": "ethereum",
          "symbol": "eth",
          "name": "Ethereum",
          "market_data": {
            "current_price": {
              "usd": 1874.52,
              "eur": 1684.43,
              "btc": 0.06270008
            },
            "circulating_supply": 120194007.919753,
            "max_supply": null
          }
        },
        "quantity": 0.5,
        "balance": 937.26,
        "balance_in_eur": 842.22,
        "balance_in_btc": 0.0313500400,
        "percentage": 23.87,
        "platform": "COINBASE"
      }
    ]
  },
  {
    "platform": "BYBIT",
    "coins": [
      {
        "coin_id": "64bd31c772a86834e9b400b5",
        "coin_info": {
          "id": "ripple",
          "symbol": "xrp",
          "name": "XRP",
          "market_data": {
            "current_price": {
              "usd": 0.739116,
              "eur": 0.664165,
              "btc": 0.00002472
            },
            "circulating_supply": 52544091958.0,
            "max_supply": 100000000000.0
          }
        },
        "quantity": 500,
        "balance": 369.56,
        "balance_in_eur": 332.08,
        "balance_in_btc": 0.0123600000,
        "percentage": 100.00,
        "platform": "BYBIT"
      }
    ]
  },
  {
    "platform": "OKX",
    "coins": [
      {
        "coin_id": "64bd31ad72a86834e9b400b4",
        "coin_info": {
          "id": "tether",
          "symbol": "usdt",
          "name": "Tether",
          "market_data": {
            "current_price": {
              "usd": 0.999949,
              "eur": 0.898548,
              "btc": 0.00003345
            },
            "circulating_supply": 83796187894.4596,
            "max_supply": null
          }
        },
        "quantity": 750,
        "balance": 749.96,
        "balance_in_eur": 673.91,
        "balance_in_btc": 0.0250875000,
        "percentage": 100.00,
        "platform": "OKX"
      }
    ]
  },
  {
    "platform": "KRAKEN",
    "coins": [
      {
        "coin_id": "64bd31e072a86834e9b400b6",
        "coin_info": {
          "id": "solana",
          "symbol": "sol",
          "name": "Solana",
          "market_data": {
            "current_price": {
              "usd": 24.64,
              "eur": 22.14,
              "btc": 0.00082406
            },
            "circulating_supply": 404138965.604631,
            "max_supply": null
          }
        },
        "quantity": 30,
        "balance": 739.20,
        "balance_in_eur": 664.20,
        "balance_in_btc": 0.0247218000,
        "percentage": 90.80,
        "platform": "KRAKEN"
      },
      {
        "coin_id": "64bd31eb72a86834e9b400b7",
        "coin_info": {
          "id": "matic-network",
          "symbol": "matic",
          "name": "Polygon",
          "market_data": {
            "current_price": {
              "usd": 0.749245,
              "eur": 0.673267,
              "btc": 0.00002506
            },
            "circulating_supply": 9319469069.28493,
            "max_supply": 10000000000.0
          }
        },
        "quantity": 100,
        "balance": 74.92,
        "balance_in_eur": 67.33,
        "balance_in_btc": 0.0025060000,
        "percentage": 9.20,
        "platform": "KRAKEN"
      }
    ]
  }
]
```

### `/api/v1/dashboards/platform/balances`

#### Retrieves balances information for all platforms

```json
{
  "totalBalance": 6466.25,
  "platforms": [
    {
      "platformName": "BINANCE",
      "percentage": 9.37,
      "balance": 605.75
    },
    {
      "platformName": "COINBASE",
      "percentage": 60.73,
      "balance": 3926.86
    },
    {
      "platformName": "KRAKEN",
      "percentage": 12.59,
      "balance": 814.12
    },
    {
      "platformName": "BYBIT",
      "percentage": 5.72,
      "balance": 369.56
    },
    {
      "platformName": "OKX",
      "percentage": 11.6,
      "balance": 749.96
    }
  ]
}
```

</details>

<br>