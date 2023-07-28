package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record CryptoBalanceResponse(
        BigDecimal totalBalance,
        BigDecimal totalEURBalance,
        BigDecimal totalBTCBalance,
        List<CryptoResponse> cryptos
) {
}
