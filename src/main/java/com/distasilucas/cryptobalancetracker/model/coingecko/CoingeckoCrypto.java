package com.distasilucas.cryptobalancetracker.model.coingecko;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public sealed class CoingeckoCrypto implements Serializable permits CoingeckoCryptoInfo {

    private String id;
    private String symbol;
    private String name;
}
