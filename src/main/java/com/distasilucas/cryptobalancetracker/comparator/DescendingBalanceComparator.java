package com.distasilucas.cryptobalancetracker.comparator;

import com.distasilucas.cryptobalancetracker.model.response.dashboard.CryptoInfoResponse;

import java.util.Comparator;

public class DescendingBalanceComparator implements Comparator<CryptoInfoResponse> {

    @Override
    public int compare(CryptoInfoResponse o1, CryptoInfoResponse o2) {
        return o2.balance().compareTo(o1.balance());
    }
}
