package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DescendingBalanceComparatorTest {

    Comparator<CoinInfoResponse> comparator = new DescendingBalanceComparator();

    @Test
    void shouldCompareSuccessfully() {
        var higherCoinInfoResponse = new CoinInfoResponse("bitcoin", BigDecimal.valueOf(0.5), BigDecimal.valueOf(100),
                0, Collections.emptySet());
        var lowerCoinInfoResponse = new CoinInfoResponse("bitcoin", BigDecimal.valueOf(0.5), BigDecimal.valueOf(50),
                0, Collections.emptySet());

        var firstCompare = comparator.compare(higherCoinInfoResponse, lowerCoinInfoResponse);
        var secondCompare = comparator.compare(lowerCoinInfoResponse, higherCoinInfoResponse);

        assertAll(
                () -> assertEquals(-1, firstCompare),
                () -> assertEquals(1, secondCompare)
        );
    }
}