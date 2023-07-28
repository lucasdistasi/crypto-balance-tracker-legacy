package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoResponse;

import java.util.Comparator;

public class DescendingPercentageComparator implements Comparator<CryptoResponse> {

    @Override
    public int compare(CryptoResponse cryptoResponse1, CryptoResponse cryptoResponse2) {
        return cryptoResponse2.getPercentage().compareTo(cryptoResponse1.getPercentage());
    }
}
