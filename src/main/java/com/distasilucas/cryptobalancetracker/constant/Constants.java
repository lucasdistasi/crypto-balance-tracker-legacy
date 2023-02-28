package com.distasilucas.cryptobalancetracker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String APPLICATION_JSON = "application/json";
    public static final String OK_CODE = "200";
    public static final String RESOURCE_CREATED_CODE = "201";
    public static final String NO_CONTENT_CODE = "204";
    public static final String BAD_REQUEST_CODE = "400";
    public static final String FORBIDDEN_CODE = "403";
    public static final String NOT_FOUND_CODE = "404";
    public static final String INTERNAL_SERVER_ERROR_CODE = "500";
    public static final String INVALID_DATA = "Invalid data";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String PLATFORM_NOT_FOUND_DESCRIPTION = "Platform not found";
    public static final String CRYPTO_NOT_FOUND_DESCRIPTION = "Crypto not found";
    public static final String CRYPTO_OR_PLATFORM_NOT_FOUND_DESCRIPTION = "Crypto or platform not found";
    public static final BigDecimal MAX_CRYPTO_QUANTITY = BigDecimal.valueOf(9999999999999999.999999999999);
    public static final String INVALID_CRYPTO_QUANTITY = String.format("Quantity must be greater than 0 and less than %s", MAX_CRYPTO_QUANTITY);
    public static final String ERROR_VALIDATING_JSON_SCHEMA = "Error validating Json Schema %s";
    public static final String UNKNOWN_ERROR = "Unknown Error";
    public static final String INVALID_PLATFORM_FORMAT = "Platform name must be only letters";
    public static final String COIN_NAME_NOT_FOUND = "Coin with name %s not found";
    public static final String NO_COIN_IN_PLATFORM = "Coin %s not found in %s";
    public static final String PLATFORM_NOT_FOUND = "Platform %s not found. Consider adding it";
    public static final String DUPLICATED_PLATFORM = "Platform %s already exists";
    public static final String DUPLICATED_PLATFORM_COIN = "You already have %s in %s";
    public static final String COINGECKO_CRYPTOS_CACHE = "coingeckoCryptos";
    public static final String CRYPTO_PRICE_CACHE = "cryptoPrice";
    public static final String MAX_RATE_LIMIT_REACHED = "You've exceeded the Rate Limit. Please visit https://www.coingecko.com/en/api/pricing to subscribe to Coingecko API plans for higher rate limits or try again later";
    public static final String TOKEN_EXPIRED = "Token is expired";
    public static final String UNKNOWN = "Unknown";

}
