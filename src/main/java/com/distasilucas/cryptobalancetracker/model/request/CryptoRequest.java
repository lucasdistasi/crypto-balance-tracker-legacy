package com.distasilucas.cryptobalancetracker.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CryptoRequest {

    private BigDecimal quantity;
    private String platform;
}
