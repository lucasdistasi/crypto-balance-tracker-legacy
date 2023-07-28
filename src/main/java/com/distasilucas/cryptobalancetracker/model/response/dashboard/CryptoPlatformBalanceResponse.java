package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record CryptoPlatformBalanceResponse(
        BigDecimal totalBalance,
        List<CryptoInfoResponse> cryptoInfoResponse
) {
}
