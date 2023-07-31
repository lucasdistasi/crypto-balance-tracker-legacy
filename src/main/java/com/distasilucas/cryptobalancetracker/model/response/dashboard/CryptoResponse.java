package com.distasilucas.cryptobalancetracker.model.response.dashboard;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCryptoInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CryptoResponse {

    private String cryptoId;
    private CoingeckoCryptoInfo cryptoInfo;
    private BigDecimal quantity;
    private BigDecimal balance;
    private BigDecimal balanceInEUR;
    private BigDecimal balanceInBTC;
    private BigDecimal percentage;
    private String platform;

    public CryptoResponse(String cryptoId, CoingeckoCryptoInfo cryptoInfo, BigDecimal quantity,
                          BigDecimal balance, BigDecimal balanceInEUR, BigDecimal balanceInBTC, String platform) {
        this.cryptoId = cryptoId;
        this.cryptoInfo = cryptoInfo;
        this.quantity = quantity;
        this.balance = balance;
        this.balanceInEUR = balanceInEUR;
        this.balanceInBTC = balanceInBTC;
        this.platform = platform;
    }
}