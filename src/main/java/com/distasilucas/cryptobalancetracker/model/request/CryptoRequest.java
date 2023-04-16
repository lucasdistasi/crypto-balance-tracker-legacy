package com.distasilucas.cryptobalancetracker.model.request;

import java.math.BigDecimal;

public record CryptoRequest(
        String coin_name,
        BigDecimal quantity,
        String platform
) {
}
