package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinResponse;

import java.util.Comparator;

public class DescendingPercentageComparator implements Comparator<CoinResponse> {

    @Override
    public int compare(CoinResponse coinResponse1, CoinResponse coinResponse2) {
        return Double.compare(coinResponse2.getPercentage(), coinResponse1.getPercentage());
    }
}
