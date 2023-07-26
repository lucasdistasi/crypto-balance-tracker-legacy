package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import java.math.BigDecimal;
import java.util.Set;

public record CryptoInfoResponse(
        String name,
        BigDecimal quantity,
        BigDecimal balance,
        BigDecimal percentage,
        Set<String> platforms
) {
}
