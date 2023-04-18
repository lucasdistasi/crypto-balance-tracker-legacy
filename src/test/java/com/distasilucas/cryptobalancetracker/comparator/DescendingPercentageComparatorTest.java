package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DescendingPercentageComparatorTest {

    Comparator<CoinResponse> comparator = new DescendingPercentageComparator();

    @Test
    void shouldCompareSuccessfully() {
        var coinInfo = MockData.getCoinInfo();
        var higherCoinResponse = MockData.getCoinResponse(coinInfo);
        higherCoinResponse.setPercentage(60);
        var lowerCoinResponse = MockData.getCoinResponse(coinInfo);
        lowerCoinResponse.setPercentage(40);

        var firstCompare = comparator.compare(higherCoinResponse, lowerCoinResponse);
        var secondCompare = comparator.compare(lowerCoinResponse, higherCoinResponse);

        assertAll(
                () -> assertEquals(-1, firstCompare),
                () -> assertEquals(1, secondCompare)
        );
    }
}