package com.distasilucas.cryptobalancetracker.model.coingecko;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public final class CoinInfo extends Coin {

    @JsonProperty("market_data")
    private MarketData marketData;
}
