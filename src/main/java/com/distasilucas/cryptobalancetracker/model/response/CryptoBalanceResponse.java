package com.distasilucas.cryptobalancetracker.model.response;

import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Setter
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CryptoBalanceResponse {

    private CoinInfo coinInfo;
    private BigDecimal quantity;
    private BigDecimal balance;
    private double percentage;

    public CryptoBalanceResponse(CoinInfo coinInfo, BigDecimal quantity, BigDecimal balance) {
        this.coinInfo = coinInfo;
        this.quantity = quantity;
        this.balance = balance;
    }
}
