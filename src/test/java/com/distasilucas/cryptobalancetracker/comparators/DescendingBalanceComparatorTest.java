package com.distasilucas.cryptobalancetracker.comparators;

import com.distasilucas.cryptobalancetracker.model.response.CoinInfoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DescendingBalanceComparatorTest {

    Comparator<CoinInfoResponse> comparator = new DescendingBalanceComparator();

    @Test
    void shouldCompareSuccessfully() {
        var higherCoinInfoResponse = CoinInfoResponse.builder()
                .balance(BigDecimal.valueOf(100))
                .build();
        var lowerCoinInfoResponse = CoinInfoResponse.builder()
                .balance(BigDecimal.valueOf(50))
                .build();

        int firstCompare = comparator.compare(higherCoinInfoResponse, lowerCoinInfoResponse);
        int secondCompare = comparator.compare(lowerCoinInfoResponse, higherCoinInfoResponse);

        assertAll(
                () -> assertEquals(-1, firstCompare),
                () -> assertEquals(1, secondCompare)
        );
    }
}