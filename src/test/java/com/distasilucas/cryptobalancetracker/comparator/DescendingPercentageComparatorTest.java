package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.MockData;
import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DescendingPercentageComparatorTest {

    Comparator<CryptoResponse> comparator = new DescendingPercentageComparator();

    @Test
    void shouldCompareSuccessfully() {
        var coingeckoCryptoInfo = MockData.getBitcoinCoingeckoCryptoInfo();
        var higherCryptoResponse = MockData.getCryptoResponse(coingeckoCryptoInfo);
        higherCryptoResponse.setPercentage(BigDecimal.valueOf(60));
        var lowerCryptoResponse = MockData.getCryptoResponse(coingeckoCryptoInfo);
        lowerCryptoResponse.setPercentage(BigDecimal.valueOf(40));

        var firstCompare = comparator.compare(higherCryptoResponse, lowerCryptoResponse);
        var secondCompare = comparator.compare(lowerCryptoResponse, higherCryptoResponse);

        assertAll(
                () -> assertEquals(-1, firstCompare),
                () -> assertEquals(1, secondCompare)
        );
    }
}