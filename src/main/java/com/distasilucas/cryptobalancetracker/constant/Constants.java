package com.distasilucas.cryptobalancetracker.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final int QUANTITY_FRACTIONAL_MAX_LENGTH = 12;
    public static final int QUANTITY_WHOLE_MAX_LENGTH = 16;
    public static final BigDecimal MAX_CRYPTO_QUANTITY = BigDecimal.valueOf(9999999999999999.999999999999);
    public static final String INVALID_CRYPTO_QUANTITY = String.format("Quantity must be greater than 0 and less than %s", MAX_CRYPTO_QUANTITY);
}
