package com.distasilucas.cryptobalancetracker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.distasilucas.cryptobalancetracker.constant.Constants.MAX_CRYPTO_QUANTITY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstants {

    public static final String PLATFORM_NOT_FOUND = "Platform %s does not exists";
    public static final String UNKNOWN_ERROR = "Unknown Error";
    public static final String TOKEN_EXPIRED = "Token is expired";
    public static final String MAX_RATE_LIMIT_REACHED = "You've exceeded the Rate Limit. Please visit https://www.coingecko.com/en/api/pricing to subscribe to Coingecko API plans for higher rate limits or try again later";
    public static final String DUPLICATED_PLATFORM = "Platform %s already exists";
    public static final String DUPLICATED_PLATFORM_COIN = "You already have %s in %s";
    public static final String COIN_NAME_NOT_FOUND = "Coin with name %s not found";
    public static final String COIN_ID_NOT_FOUND = "Coin with ID %s not found";
    public static final String COIN_NOT_FOUND = "Coin not found";
    public static final String INVALID_PLATFORM_FORMAT = "Platform name must be 1-24 characters long, no numbers, special characters or whitespace allowed.";
    public static final String ERROR_VALIDATING_JSON_SCHEMA = "Error validating Json Schema %s";
    public static final String INVALID_CRYPTO_QUANTITY = String.format("Quantity must be greater than 0 and less than %s", MAX_CRYPTO_QUANTITY);
    public static final String USERNAME_NOT_FOUND = "Username not found";
    public static final String INVALID_CRYPTO_ID_FORMAT = "Invalid crypto ID format";

}
