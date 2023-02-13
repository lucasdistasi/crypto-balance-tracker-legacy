package com.distasilucas.cryptobalancetracker.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CryptoDTO {

    private String coinName;

    private BigDecimal quantity;

    private String platform;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String ticker;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String coinId;
}
