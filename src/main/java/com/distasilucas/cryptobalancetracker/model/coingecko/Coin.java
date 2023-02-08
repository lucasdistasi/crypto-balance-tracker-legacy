package com.distasilucas.cryptobalancetracker.model.coingecko;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coin implements Serializable {

    private String id;
    private String symbol;
    private String name;
}
