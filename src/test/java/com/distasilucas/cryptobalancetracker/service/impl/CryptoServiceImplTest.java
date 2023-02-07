package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.model.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CryptoServiceImplTest {

    @Mock
    CoingeckoService coingeckoServiceMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    Validation<CryptoDTO> addCryptoValidationMock;

    CryptoService<Crypto, CryptoDTO> cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoServiceImpl(coingeckoServiceMock, cryptoRepositoryMock, addCryptoValidationMock);
    }

    @Test
    void shouldAddCrypto() {
        var coins = getCoins();
        var cryptoDTO = CryptoDTO.builder()
                .ticker("btc")
                .quantity(BigDecimal.valueOf(2))
                .build();
        var expectedCrypto = Crypto.builder()
                .ticker("btc")
                .name("Bitcoin")
                .coinId("bitcoin")
                .quantity(BigDecimal.valueOf(2))
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoDTO);
        when(coingeckoServiceMock.retrieveAllCryptos()).thenReturn(coins);

        var actualCrypto = cryptoService.addCrypto(cryptoDTO);

        verify(cryptoRepositoryMock, times(1)).save(actualCrypto);
        assertAll(
                () -> assertEquals(expectedCrypto.getName(), actualCrypto.getName())
        );
    }

    @Test
    void shouldThrowCoinNotFoundExceptionForUnknownCoin() {
        var coins = getCoins();
        var cryptoDTO = CryptoDTO.builder()
                .ticker("xyz")
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoDTO);
        when(coingeckoServiceMock.retrieveAllCryptos()).thenReturn(coins);

        var coinNotFoundException = assertThrows(
                CoinNotFoundException.class,
                () -> cryptoService.addCrypto(cryptoDTO)
        );

        assertAll(
                () -> assertEquals(coinNotFoundException.getErrorMessage(), "Coin not found for ticker xyz")
        );
    }

    private List<Coin> getCoins() {
        Coin coin = new Coin("bitcoin", "btc", "Bitcoin");

        return Collections.singletonList(coin);
    }
}