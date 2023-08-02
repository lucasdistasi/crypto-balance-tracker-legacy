package com.distasilucas.cryptobalancetracker.model.response.crypto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PageCryptoResponse {

    private final int page;
    private final int totalPages;
    private final boolean hasNextPage;
    private final List<UserCryptoResponse> cryptos;

    public PageCryptoResponse(int page, int totalPages, List<UserCryptoResponse> cryptos) {
        this.page = page + 1;
        this.totalPages = totalPages;
        this.hasNextPage = page < totalPages - 1;
        this.cryptos = cryptos;
    }
}
