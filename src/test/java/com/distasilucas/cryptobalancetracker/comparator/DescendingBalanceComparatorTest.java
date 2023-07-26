package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoInfoResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DescendingBalanceComparatorTest {

    Comparator<CryptoInfoResponse> comparator = new DescendingBalanceComparator();

    @Test
    void shouldCompareSuccessfully() {
        var higherCryptoInfoResponse = new CryptoInfoResponse("bitcoin", BigDecimal.valueOf(0.5), BigDecimal.valueOf(100),
                BigDecimal.ZERO, Collections.emptySet());
        var lowerCryptoInfoResponse = new CryptoInfoResponse("bitcoin", BigDecimal.valueOf(0.5), BigDecimal.valueOf(50),
                BigDecimal.ZERO, Collections.emptySet());

        var firstCompare = comparator.compare(higherCryptoInfoResponse, lowerCryptoInfoResponse);
        var secondCompare = comparator.compare(lowerCryptoInfoResponse, higherCryptoInfoResponse);

        assertAll(
                () -> assertEquals(-1, firstCompare),
                () -> assertEquals(1, secondCompare)
        );
    }
}