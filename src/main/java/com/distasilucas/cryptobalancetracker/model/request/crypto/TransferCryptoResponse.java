package com.distasilucas.cryptobalancetracker.model.request.crypto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferCryptoResponse {

    private FromPlatform fromPlatform;
    private ToPlatform toPlatform;

}
