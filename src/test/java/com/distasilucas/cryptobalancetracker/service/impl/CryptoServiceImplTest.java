package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoinInfo;
import com.distasilucas.cryptobalancetracker.model.coingecko.CurrentPrice;
import com.distasilucas.cryptobalancetracker.model.coingecko.MarketData;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.model.response.CoinResponse;
import com.distasilucas.cryptobalancetracker.model.response.CryptoBalanceResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoServiceImplTest {

    @Mock
    EntityMapper<Crypto, CryptoDTO> cryptoMapperImplMock;

    @Mock
    EntityMapper<CryptoDTO, Crypto> cryptoDTOMapperImplMock;

    @Mock
    EntityMapper<CryptoBalanceResponse, List<Crypto>> cryptoBalanceResponseMapperImplMock;

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    Validation<CryptoDTO> addCryptoValidationMock;

    @Mock
    Validation<CryptoDTO> updateCryptoValidationMock;

    CryptoService<CryptoDTO> cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoServiceImpl(cryptoMapperImplMock, cryptoDTOMapperImplMock, cryptoBalanceResponseMapperImplMock,
                cryptoRepositoryMock, addCryptoValidationMock, updateCryptoValidationMock);
    }

    @Test
    void shouldAddCrypto() {
        var cryptoDTO = CryptoDTO.builder()
                .coinName("Bitcoin")
                .build();
        var cryptoEntity = Crypto.builder()
                .name("Bitcoin")
                .build();

        doNothing().when(addCryptoValidationMock).validate(cryptoDTO);
        when(cryptoMapperImplMock.mapFrom(cryptoDTO)).thenReturn(cryptoEntity);
        when(cryptoDTOMapperImplMock.mapFrom(cryptoEntity)).thenReturn(cryptoDTO);

        var actualCrypto = cryptoService.addCoin(cryptoDTO);

        assertAll(
                () -> assertEquals(cryptoDTO.getCoinName(), actualCrypto.getCoinName()),
                () -> verify(addCryptoValidationMock, times(1)).validate(cryptoDTO),
                () -> verify(cryptoRepositoryMock, times(1)).save(cryptoEntity),
                () -> verify(cryptoMapperImplMock, times(1)).mapFrom(cryptoDTO),
                () -> verify(cryptoDTOMapperImplMock, times(1)).mapFrom(cryptoEntity)
        );
    }

    @Test
    void shouldRetrieveCryptoBalances() {
        var coinInfo = getCoinInfo();
        var cryptos = getCryptos();
        var balanceResponse = getCryptoBalanceResponse();
        var firstCoin = balanceResponse.getCoins().get(0);

        when(cryptoRepositoryMock.findAll()).thenReturn(cryptos);
        when(cryptoBalanceResponseMapperImplMock.mapFrom(cryptos)).thenReturn(balanceResponse);

        var cryptoBalanceResponse = cryptoService.retrieveCoinsBalances();
        var expectedBalance = getTotalMoney(Collections.singletonList(firstCoin));

        assertAll(
                () -> assertEquals(expectedBalance, cryptoBalanceResponse.getTotalBalance()),
                () -> assertEquals(BigDecimal.valueOf(1), firstCoin.getQuantity()),
                () -> assertEquals(100, firstCoin.getPercentage()),
                () -> assertEquals(coinInfo, firstCoin.getCoinInfo())
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

    @Test
    void shouldUpdateCoin() {
        var cryptoDTO = CryptoDTO.builder()
                .coinName("Bitcoin")
                .quantity(BigDecimal.valueOf(2))
                .build();
        var crypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(1))
                .build();

        doNothing().when(updateCryptoValidationMock).validate(cryptoDTO);
        when(cryptoRepositoryMock.findByName(cryptoDTO.getCoinName())).thenReturn(Optional.of(crypto));
        when(cryptoDTOMapperImplMock.mapFrom(crypto)).thenReturn(cryptoDTO);

        var updatedCrypto = cryptoService.updateCoin(cryptoDTO, "Bitcoin");

        assertAll(
                () -> assertEquals(cryptoDTO.getQuantity(), updatedCrypto.getQuantity()),
                () -> verify(cryptoRepositoryMock, times(1)).save(crypto)
        );

    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenUpdatingNonExistentCoin() {
        var cryptoDTO = CryptoDTO.builder()
                .coinName("Dogecoin")
                .build();

        doNothing().when(updateCryptoValidationMock).validate(cryptoDTO);
        when(cryptoRepositoryMock.findByName(cryptoDTO.getCoinName())).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(
                CoinNotFoundException.class,
                () -> cryptoService.updateCoin(cryptoDTO, "Dogecoin")
        );

        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, cryptoDTO.getCoinName());
        assertAll(
                () -> assertEquals(coinNotFoundException.getErrorMessage(), expectedMessage)
        );
    }

    @Test
    void shouldDeleteCoin() {
        var cryptoEntity = Crypto.builder()
                .name("Shiba")
                .build();

        when(cryptoRepositoryMock.findByName("Shiba")).thenReturn(Optional.of(cryptoEntity));

        cryptoService.deleteCoin("Shiba");

        verify(cryptoRepositoryMock, times(1)).delete(cryptoEntity);
    }

    @Test
    void shouldThrowCoinNotFoundExceptionWhenDeleting() {
        when(cryptoRepositoryMock.findByName("Shiba")).thenReturn(Optional.empty());

        var coinNotFoundException = assertThrows(CoinNotFoundException.class, () -> cryptoService.deleteCoin("Shiba"));

        assertAll(
                () -> assertEquals(String.format(COIN_NAME_NOT_FOUND, "Shiba"), coinNotFoundException.getErrorMessage())
        );
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

    private CryptoBalanceResponse getCryptoBalanceResponse() {
        var coinInfo = getCoinInfo();
        var coinResponse = new CoinResponse(coinInfo, BigDecimal.valueOf(1), BigDecimal.valueOf(1000), "Binance");
        coinResponse.setPercentage(100);

        var coins = Collections.singletonList(coinResponse);
        var cryptoBalanceResponse = new CryptoBalanceResponse();
        cryptoBalanceResponse.setTotalBalance(BigDecimal.valueOf(1000));
        cryptoBalanceResponse.setCoins(coins);

        return cryptoBalanceResponse;
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

    private static BigDecimal getTotalMoney(List<CoinResponse> coinsResponse) {
        return coinsResponse.stream()
                .map(CoinResponse::getBalance)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }
}