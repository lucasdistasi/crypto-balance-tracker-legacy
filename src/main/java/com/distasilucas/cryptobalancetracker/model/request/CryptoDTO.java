package com.distasilucas.cryptobalancetracker.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CryptoDTO(
        String coin_name,
        BigDecimal quantity,
        String platform,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String ticker,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String coinId,

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        BigDecimal lastKnownPrice
) {
}
