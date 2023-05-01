package com.distasilucas.cryptobalancetracker.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateCryptoRequest extends CryptoRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cryptoId;

    public UpdateCryptoRequest(String cryptoId, BigDecimal quantity, String platform) {
        super(quantity, platform);
        this.cryptoId = cryptoId;
    }
}
