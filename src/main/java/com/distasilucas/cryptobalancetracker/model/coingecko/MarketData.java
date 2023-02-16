package com.distasilucas.cryptobalancetracker.model.coingecko;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record MarketData(@JsonProperty("current_price") CurrentPrice currentPrice) implements Serializable {
}
