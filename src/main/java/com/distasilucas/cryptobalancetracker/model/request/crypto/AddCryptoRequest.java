package com.distasilucas.cryptobalancetracker.model.request.crypto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddCryptoRequest extends CryptoRequest {

    private String cryptoName;

    public AddCryptoRequest(String cryptoName, BigDecimal quantity, String platform) {
        super(quantity, platform);
        this.cryptoName = cryptoName;
    }
}
