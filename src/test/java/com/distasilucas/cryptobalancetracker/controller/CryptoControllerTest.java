package com.distasilucas.cryptobalancetracker.controller;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.model.CryptoDTO;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    @Mock
    CryptoService<Crypto, CryptoDTO> cryptoServiceMocK;

    CryptoController cryptoController;

    @BeforeEach
    void setUp() {
        cryptoController = new CryptoController(cryptoServiceMocK);
    }

    @Test
    void shouldReturnCreatedCrypto() {
        var cryptoDTO = CryptoDTO.builder()
                .ticker("btc")
                .build();
        var crypto = Crypto.builder()
                .ticker("btc")
                .build();

        when(cryptoServiceMocK.addCrypto(cryptoDTO)).thenReturn(crypto);

        var cryptoResponseEntity = cryptoController.addCrypto(cryptoDTO);

        assertNotNull(cryptoResponseEntity.getBody());
        assertAll("cryptoResponseEntity",
                () -> assertEquals(cryptoResponseEntity.getStatusCode(), HttpStatus.CREATED),
                () -> assertEquals(cryptoResponseEntity.getStatusCodeValue(), HttpStatus.CREATED.value()),
                () -> assertEquals(cryptoResponseEntity.getBody().getTicker(), cryptoDTO.getTicker())
        );
    }
}