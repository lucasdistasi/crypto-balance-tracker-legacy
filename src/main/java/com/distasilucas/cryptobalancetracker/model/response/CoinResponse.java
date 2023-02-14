package com.distasilucas.cryptobalancetracker.model.response;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CoinResponse {

    private CoinInfo coinInfo;
    private BigDecimal quantity;
    private BigDecimal balance;
    private double percentage;

    private String platform;

    public CoinResponse(CoinInfo coinInfo, BigDecimal quantity, BigDecimal balance, String platform) {
        this.coinInfo = coinInfo;
        this.quantity = quantity;
        this.balance = balance;
        this.platform = platform;
    }
}
