package com.distasilucas.cryptobalancetracker.model.coingecko;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoinInfo extends Coin {

    @JsonProperty("market_data")
    private MarketData marketData;
}
