package com.distasilucas.cryptobalancetracker.model.response.crypto;

import java.math.BigDecimal;
import java.util.Set;

public record CoinInfoResponse(
        String name,
        BigDecimal quantity,
        BigDecimal balance,
        double percentage,
        Set<String> platforms
) {
}
