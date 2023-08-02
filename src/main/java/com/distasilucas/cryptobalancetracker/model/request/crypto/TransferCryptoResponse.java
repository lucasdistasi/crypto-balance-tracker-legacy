package com.distasilucas.cryptobalancetracker.model.request.crypto;

import com.distasilucas.cryptobalancetracker.model.FromPlatform;
import com.distasilucas.cryptobalancetracker.model.ToPlatform;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransferCryptoResponse {

    private FromPlatform fromPlatform;
    private ToPlatform toPlatform;

}
