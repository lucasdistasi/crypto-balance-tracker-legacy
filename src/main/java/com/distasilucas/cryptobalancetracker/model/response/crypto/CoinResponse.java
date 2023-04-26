package com.distasilucas.cryptobalancetracker.model.response.crypto;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CoinResponse {

    private String coinId;
    private CoinInfo coinInfo;
    private BigDecimal quantity;
    private BigDecimal balance;
    private BigDecimal balanceInEUR;
    private BigDecimal balanceInBTC;
    private double percentage;
    private String platform;

    public CoinResponse(String coinId, CoinInfo coinInfo, BigDecimal quantity,
                        BigDecimal balance, BigDecimal balanceInEUR, BigDecimal balanceInBTC, String platform) {
        this.coinId = coinId;
        this.coinInfo = coinInfo;
        this.quantity = quantity;
        this.balance = balance;
        this.balanceInEUR = balanceInEUR;
        this.balanceInBTC = balanceInBTC;
        this.platform = platform;
    }
}
