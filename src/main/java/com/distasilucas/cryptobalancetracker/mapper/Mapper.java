package com.distasilucas.cryptobalancetracker.mapper;

public interface Mapper<T, U> {

    T mapTo(U u);
}
