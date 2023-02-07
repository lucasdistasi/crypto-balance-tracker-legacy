package com.distasilucas.cryptobalancetracker.service;

public interface CryptoService<T, U> {

    T addCrypto(U input);
}
