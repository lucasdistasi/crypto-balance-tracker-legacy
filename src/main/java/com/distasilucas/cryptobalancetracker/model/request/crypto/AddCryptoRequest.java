package com.distasilucas.cryptobalancetracker.model.request.crypto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddCryptoRequest extends CryptoRequest {

    private String coinName;

    public AddCryptoRequest(String coinName, BigDecimal quantity, String platform) {
        super(quantity, platform);
        this. coinName = coinName;
    }
}
