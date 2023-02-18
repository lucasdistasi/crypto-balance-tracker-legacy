package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.MockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.distasilucas.cryptobalancetracker.constant.Constants.COIN_NAME_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoMapperImplTest {

    @Mock
    CoingeckoService coingeckoServiceMock;

    @Mock
    PlatformService platformServiceMock;

    EntityMapper<Crypto, CryptoDTO> entityMapper;

    @BeforeEach
    void setUp() {
        entityMapper = new CryptoMapperImpl(coingeckoServiceMock, platformServiceMock);
    }

    @Test
    void shouldMapSuccessfully() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platformName = cryptoDTO.platform();
        var platform = Platform.builder()
                .id("1234")
                .name(platformName)
                .build();
        var allCoins = MockData.getAllCoins();

        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(allCoins);

        var crypto = entityMapper.mapFrom(cryptoDTO);

        assertAll(
                () -> assertEquals(cryptoDTO.coin_name(), crypto.getName()),
                () -> assertEquals(cryptoDTO.coinId(), crypto.getCoinId()),
                () -> assertEquals(cryptoDTO.ticker(), crypto.getTicker()),
                () -> assertEquals(cryptoDTO.quantity(), crypto.getQuantity()),
                () -> assertEquals(platform.getId(), crypto.getPlatformId())
        );
    }

    @Test
    void shouldThrowExceptionWhenMappingNonExistentCoin() {
        var cryptoDTO = MockData.getCryptoDTO();
        var platformName = cryptoDTO.platform();
        var platform = MockData.getPlatform(platformName);

        when(platformServiceMock.findPlatformByName(platformName)).thenReturn(platform);
        when(coingeckoServiceMock.retrieveAllCoins()).thenReturn(Collections.emptyList());

        CoinNotFoundException coinNotFoundException = assertThrows(CoinNotFoundException.class,
                () -> entityMapper.mapFrom(cryptoDTO));

        var expectedMessage = String.format(COIN_NAME_NOT_FOUND, cryptoDTO.coin_name());

        assertEquals(expectedMessage, coinNotFoundException.getErrorMessage());
    }
}