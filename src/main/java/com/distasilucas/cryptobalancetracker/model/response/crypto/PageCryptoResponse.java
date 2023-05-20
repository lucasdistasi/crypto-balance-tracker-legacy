package com.distasilucas.cryptobalancetracker.model.response.crypto;

import lombok.Getter;

import java.util.List;

@Getter
public class PageCryptoResponse {

    private final int page;
    private final int totalPages;
    private final boolean hasNextPage;
    private final List<CryptoResponse> cryptos;

    public PageCryptoResponse(int page, int totalPages, List<CryptoResponse> cryptos) {
        this.page = page + 1;
        this.totalPages = totalPages;
        this.hasNextPage = page < totalPages - 1;
        this.cryptos = cryptos;
    }
}
