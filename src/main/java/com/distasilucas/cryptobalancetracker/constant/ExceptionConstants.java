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
    public static final String DUPLICATED_PLATFORM_CRYPTO = "You already have the specified cryptoId in %s";
    public static final String CRYPTO_NAME_NOT_FOUND = "Crypto with name %s not found";
    public static final String CRYPTO_ID_NOT_FOUND = "Crypto with ID %s not found";
    public static final String CRYPTO_NOT_FOUND = "Crypto not found";
    public static final String INVALID_CRYPTO_NAME = "Invalid crypto name";
    public static final String INVALID_PLATFORM_FORMAT = "Platform name must be 1-24 characters long, no numbers, special characters or whitespace allowed.";
    public static final String ERROR_VALIDATING_JSON_SCHEMA = "Error validating Json Schema %s";
    public static final String INVALID_CRYPTO_QUANTITY = String.format("Quantity must be greater than 0 and less than %s", MAX_CRYPTO_QUANTITY);
    public static final String USERNAME_NOT_FOUND = "Username not found";
    public static final String INVALID_ID_MONGO_FORMAT = "Invalid ID format";
    public static final String GOAL_ID_NOT_FOUND = "Goal with ID %s not found";
    public static final String DUPLICATED_GOAL = "Goal for %s already exists";
    public static final String GOAL_CRYPTO_NOT_FOUND = "You must own %s to set a Goal";
    public static final String NETWORK_FEE_HIGHER = "Network fee can't be higher than quantity to send";
    public static final String NOT_ENOUGH_BALANCE = "You don't have enough balance to perform this action";
    public static final String TARGET_PLATFORM_NOT_EXISTS = "Target platform does not exists";
    public static final String SAME_FROM_TO_PLATFORM = "From platform and to platform cannot be the same";
    public static final String INVALID_FEE_QUANTITY = "Invalid network fee quantity";
}
