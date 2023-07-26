package com.distasilucas.cryptobalancetracker.model.request.crypto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddCryptoRequest extends CryptoRequest {

    private String cryptoName;

    public AddCryptoRequest(String cryptoName, BigDecimal quantity, String platform) {
        super(quantity, platform);
        this.cryptoName = cryptoName;
    }
}
