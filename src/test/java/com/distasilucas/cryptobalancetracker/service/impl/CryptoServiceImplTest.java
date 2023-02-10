package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.response.CoinsResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
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

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;
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
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(2))
                .build();
        var expectedCrypto = Crypto.builder()
                .ticker("btc")
                .name("Bitcoin")
                .coinId("bitcoin")
                .quantity(BigDecimal.valueOf(2))
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoDTO);
        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(coins);

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
                .name("xyz")
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoDTO);
        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(coins);

        var coinNotFoundException = assertThrows(
                CoinNotFoundException.class,
                () -> cryptoService.addCrypto(cryptoDTO)
        );

        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, cryptoDTO.getName());
        assertAll(
                () -> assertEquals(coinNotFoundException.getErrorMessage(), expectedMessage)
        );
    }

    @Test
    void shouldRetrieveCryptoBalances() {
        var coinInfo = getCoinInfo();

        when(cryptoRepositoryMock.findAll()).thenReturn(getCryptos());
        when(coingeckoServiceMock.retrieveCoinInfo("bitcoin")).thenReturn(coinInfo);

        var cryptoBalanceResponses = cryptoService.retrieveCoinsBalances();
        var cryptoBalanceResponse = cryptoBalanceResponses.getCoins().get(0);
        var expectedBalance = getTotalMoney(Collections.singletonList(cryptoBalanceResponse));

        assertAll(
                () -> assertEquals(expectedBalance, cryptoBalanceResponse.getBalance()),
                () -> assertEquals(BigDecimal.valueOf(1.15), cryptoBalanceResponse.getQuantity()),
                () -> assertEquals(100, cryptoBalanceResponse.getPercentage()),
                () -> assertEquals(coinInfo, cryptoBalanceResponse.getCoinInfo())
        );
    }

    @Test
    void shouldReturnEmptyListIfNoCryptosAreSaved() {
        when(cryptoRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        var cryptoBalanceResponses = cryptoService.retrieveCoinsBalances();

        assertAll(
                () -> assertNull(cryptoBalanceResponses)
        );
    }

    private List<Coin> getCoins() {
        Coin coin = new Coin("bitcoin", "btc", "Bitcoin");

        return Collections.singletonList(coin);
    }

    private List<Crypto> getCryptos() {
        var crypto = Crypto.builder()
                .ticker("btc")
                .name("Bitcoin")
                .coinId("bitcoin")
                .quantity(BigDecimal.valueOf(1.15))
                .build();

        return Collections.singletonList(crypto);
    }

    private CoinInfo getCoinInfo() {
        var currentPrice = new CurrentPrice();
        currentPrice.setUsd(BigDecimal.valueOf(150000));

        var marketData = new MarketData();
        marketData.setCurrentPrice(currentPrice);

        var coinInfo = new CoinInfo();
        coinInfo.setMarketData(marketData);
        coinInfo.setSymbol("btc");
        coinInfo.setName("Bitcoin");
        coinInfo.setId("bitcoin");

        return coinInfo;
    }

    private static BigDecimal getTotalMoney(List<CoinsResponse> coinsResponse) {
        return coinsResponse.stream()
                .map(CoinsResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }
}