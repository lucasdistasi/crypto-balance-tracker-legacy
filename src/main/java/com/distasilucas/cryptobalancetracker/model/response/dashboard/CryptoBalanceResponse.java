package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record CryptoBalanceResponse(
        @JsonProperty("total_balance") BigDecimal totalBalance,
        @JsonProperty("total_EUR_balance") BigDecimal totalEURBalance,
        @JsonProperty("total_BTC_balance") BigDecimal totalBTCBalance,
        List<CryptoResponse> cryptos
) {
}
