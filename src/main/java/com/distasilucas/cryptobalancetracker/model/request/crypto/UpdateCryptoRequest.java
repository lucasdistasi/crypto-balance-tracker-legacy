package com.distasilucas.cryptobalancetracker.model.request.crypto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateCryptoRequest extends CryptoRequest {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String cryptoId;

    public UpdateCryptoRequest(String cryptoId, BigDecimal quantity, String platform) {
        super(quantity, platform);
        this.cryptoId = cryptoId;
    }
}
