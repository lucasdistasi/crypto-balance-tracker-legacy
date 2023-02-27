package com.distasilucas.cryptobalancetracker.mapper;

import java.util.function.BiFunction;

public interface BiFunctionMapper<T, U, R> {

    BiFunction<T, U, R> map();
}
