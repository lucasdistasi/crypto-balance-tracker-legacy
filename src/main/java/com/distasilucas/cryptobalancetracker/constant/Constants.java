package com.distasilucas.cryptobalancetracker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final BigDecimal MAX_CRYPTO_QUANTITY = BigDecimal.valueOf(9999999999999999.999999999999);
    public static final String COINGECKO_CRYPTOS_CACHE = "coingeckoCryptos";
    public static final String CRYPTO_PRICE_CACHE = "cryptoPrice";
    public static final String UNKNOWN = "Unknown";

}
