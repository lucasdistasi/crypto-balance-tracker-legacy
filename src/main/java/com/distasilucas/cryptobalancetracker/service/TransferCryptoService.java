package com.distasilucas.cryptobalancetracker.service;

import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoResponse;

public interface TransferCryptoService {

    TransferCryptoResponse transferCrypto(TransferCryptoRequest transferCryptoRequest);
}
