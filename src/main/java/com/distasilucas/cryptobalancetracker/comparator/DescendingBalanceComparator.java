package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.model.response.crypto.CoinInfoResponse;

import java.util.Comparator;

public class DescendingBalanceComparator implements Comparator<CoinInfoResponse> {

    @Override
    public int compare(CoinInfoResponse o1, CoinInfoResponse o2) {
        return o2.balance().compareTo(o1.balance());
    }
}
