package com.distasilucas.cryptobalancetracker.model.coingecko;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

public record MarketData(
        @JsonProperty("current_price")
        CurrentPrice currentPrice,

        @JsonProperty("total_supply")
        BigDecimal totalSupply,

        @JsonProperty("max_supply")
        BigDecimal maxSupply
) implements Serializable {
}
