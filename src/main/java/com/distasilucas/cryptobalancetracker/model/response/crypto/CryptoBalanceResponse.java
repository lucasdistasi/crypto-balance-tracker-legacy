package com.distasilucas.cryptobalancetracker.model.response.crypto;

import java.math.BigDecimal;
import java.util.List;

public record CryptoBalanceResponse(
        BigDecimal totalBalance,
        List<CoinResponse> coins
) {
}
