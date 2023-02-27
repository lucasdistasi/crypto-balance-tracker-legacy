package com.distasilucas.cryptobalancetracker.comparators;

import com.distasilucas.cryptobalancetracker.model.response.CoinInfoResponse;

import java.util.Comparator;

public class DescendingBalanceComparator implements Comparator<CoinInfoResponse> {

    @Override
    public int compare(CoinInfoResponse o1, CoinInfoResponse o2) {
        return o2.getBalance().compareTo(o1.getBalance());
    }
}
