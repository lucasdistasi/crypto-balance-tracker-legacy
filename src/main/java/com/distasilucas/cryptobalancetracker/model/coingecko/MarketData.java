package com.distasilucas.cryptobalancetracker.model.coingecko;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MarketData implements Serializable {

    private CurrentPrice currentPrice;
}
