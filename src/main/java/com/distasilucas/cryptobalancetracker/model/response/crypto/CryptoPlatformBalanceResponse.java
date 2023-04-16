package com.distasilucas.cryptobalancetracker.model.response.crypto;

import java.math.BigDecimal;
import java.util.List;

public record CryptoPlatformBalanceResponse(
        BigDecimal totalBalance,
        List<CoinInfoResponse> coinInfoResponse
) {
}
