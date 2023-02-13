package com.distasilucas.cryptobalancetracker.mapper;

public interface EntityMapper<T, U> {

    T mapFrom(U input);
}
