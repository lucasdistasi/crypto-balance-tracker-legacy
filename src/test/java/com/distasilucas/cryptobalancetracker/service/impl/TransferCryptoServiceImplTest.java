package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.InsufficientBalanceException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.TransferCryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND_IN_PLATFORM;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.NOT_ENOUGH_BALANCE;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.ORIGIN_PLATFORM_NOT_EXISTS;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.SAME_FROM_TO_PLATFORM;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.TARGET_PLATFORM_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferCryptoServiceImplTest {

    @Mock
    CryptoRepository cryptoRepositoryMock;

    @Mock
    PlatformRepository platformRepositoryMock;

    @Mock
    Validation<TransferCryptoRequest> transferCryptoValidationMock;

    TransferCryptoService transferCryptoService;

    @BeforeEach
    void setUp() {
        transferCryptoService = new TransferCryptoServiceImpl(cryptoRepositoryMock, platformRepositoryMock, transferCryptoValidationMock);
    }

    //      FROM        |       TO
    //  has remaining   |   has the crypto      ---> Update FROM and TO.
    //  has remaining   |   hasn't the crypto   ---> Update FROM. Add TO.
    //  no remaining    |   has the crypto      ---> Remove it from FROM. Update TO.
    //  no remaining    |   hasn't the crypto   ---> Maybe it's easier to update FROM with the new platform and quantity

    @Test
    // from has remaining   |   to has the crypto      ---> Update FROM and TO
    void shouldTransferToPlatformWithExistingCryptoAndHaveRemaining() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(0.5),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        var fromPlatform = Platform.builder()
                .id("PLTFRM123")
                .name(transferCryptoRequest.getFromPlatform())
                .build();
        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();
        var fromPlatformCrypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(1.25))
                .build();
        var toPlatformCrypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(0.15))
                .build();

        var networkFee = transferCryptoRequest.getNetworkFee();
        var quantityToTransfer = transferCryptoRequest.getQuantityToTransfer();
        var actualCryptoQuantity = fromPlatformCrypto.getQuantity();
        var totalToSubtract = networkFee.add(quantityToTransfer);
        var remainingCryptoQuantity = actualCryptoQuantity.subtract(totalToSubtract);
        var quantityToSendReceive = quantityToTransfer.subtract(networkFee);

        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.of(fromPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), fromPlatform.getId()))
                .thenReturn(Optional.of(fromPlatformCrypto));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), toPlatform.getId()))
                .thenReturn(Optional.of(toPlatformCrypto));

        var transferCryptoResponse = transferCryptoService.transferCrypto(transferCryptoRequest);

        toPlatformCrypto.setQuantity(quantityToSendReceive);
        fromPlatformCrypto.setQuantity(remainingCryptoQuantity);

        verify(cryptoRepositoryMock, times(1))
                .saveAll(Arrays.asList(toPlatformCrypto, fromPlatformCrypto));
        assertAll(
                () -> assertEquals(transferCryptoRequest.getNetworkFee(), transferCryptoResponse.getFromPlatform().getNetworkFee()),
                () -> assertEquals(transferCryptoRequest.getQuantityToTransfer(), transferCryptoResponse.getFromPlatform().getQuantityToTransfer()),
                () -> assertEquals(BigDecimal.valueOf(0.501), transferCryptoResponse.getFromPlatform().getTotalToSubtract()),
                () -> assertEquals(quantityToSendReceive, transferCryptoResponse.getFromPlatform().getQuantityToSendReceive()),
                () -> assertEquals(BigDecimal.valueOf(0.749), transferCryptoResponse.getFromPlatform().getRemainingCryptoQuantity()),
                () -> assertEquals(BigDecimal.valueOf(0.649), transferCryptoResponse.getToPlatform().getNewQuantity())
        );
    }

    @Test
    // from has remaining   |   to hasn't the crypto   ---> Update FROM. Add TO
    void shouldTransferToPlatformWithoutExistingCryptoAndHaveRemaining() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(0.5),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        var fromPlatform = Platform.builder()
                .id("PLTFRM123")
                .name(transferCryptoRequest.getFromPlatform())
                .build();
        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();
        var fromPlatformCrypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(1.25))
                .build();

        var networkFee = transferCryptoRequest.getNetworkFee();
        var quantityToTransfer = transferCryptoRequest.getQuantityToTransfer();
        var quantityToSendReceive = quantityToTransfer.subtract(networkFee);

        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.of(fromPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), fromPlatform.getId()))
                .thenReturn(Optional.of(fromPlatformCrypto));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), toPlatform.getId()))
                .thenReturn(Optional.empty());

        var transferCryptoResponse = transferCryptoService.transferCrypto(transferCryptoRequest);

        verify(cryptoRepositoryMock, times(1)).saveAll(any());

        assertAll(
                () -> assertEquals(transferCryptoRequest.getNetworkFee(), transferCryptoResponse.getFromPlatform().getNetworkFee()),
                () -> assertEquals(transferCryptoRequest.getQuantityToTransfer(), transferCryptoResponse.getFromPlatform().getQuantityToTransfer()),
                () -> assertEquals(BigDecimal.valueOf(0.501), transferCryptoResponse.getFromPlatform().getTotalToSubtract()),
                () -> assertEquals(quantityToSendReceive, transferCryptoResponse.getFromPlatform().getQuantityToSendReceive()),
                () -> assertEquals(BigDecimal.valueOf(0.749), transferCryptoResponse.getFromPlatform().getRemainingCryptoQuantity()),
                () -> assertEquals(BigDecimal.valueOf(0.499), transferCryptoResponse.getToPlatform().getNewQuantity())
        );
    }

    @Test
    // from no remaining    |   has the crypto      ---> Remove it from FROM. Update TO
    void shouldTransferToPlatformWithExistingCryptoAndNoRemaining() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(1.25),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        var fromPlatform = Platform.builder()
                .id("PLTFRM123")
                .name(transferCryptoRequest.getFromPlatform())
                .build();
        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();
        var fromPlatformCrypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(1.25))
                .build();
        var toPlatformCrypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(0.15))
                .build();

        var networkFee = transferCryptoRequest.getNetworkFee();
        var quantityToSendReceive = transferCryptoRequest.getQuantityToTransfer().subtract(networkFee);

        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.of(fromPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), fromPlatform.getId()))
                .thenReturn(Optional.of(fromPlatformCrypto));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), toPlatform.getId()))
                .thenReturn(Optional.of(toPlatformCrypto));

        var transferCryptoResponse = transferCryptoService.transferCrypto(transferCryptoRequest);

        toPlatformCrypto.setQuantity(BigDecimal.valueOf(1.399));
        fromPlatformCrypto.setQuantity(BigDecimal.ZERO);

        verify(cryptoRepositoryMock, times(1)).delete(fromPlatformCrypto);
        verify(cryptoRepositoryMock, times(1)).save(toPlatformCrypto);
        assertAll(
                () -> assertEquals(transferCryptoRequest.getNetworkFee(), transferCryptoResponse.getFromPlatform().getNetworkFee()),
                () -> assertEquals(transferCryptoRequest.getQuantityToTransfer(), transferCryptoResponse.getFromPlatform().getQuantityToTransfer()),
                () -> assertEquals(BigDecimal.valueOf(1.25), transferCryptoResponse.getFromPlatform().getTotalToSubtract()),
                () -> assertEquals(quantityToSendReceive, transferCryptoResponse.getFromPlatform().getQuantityToSendReceive()),
                () -> assertEquals(BigDecimal.ZERO, transferCryptoResponse.getFromPlatform().getRemainingCryptoQuantity()),
                () -> assertEquals(BigDecimal.valueOf(1.399), transferCryptoResponse.getToPlatform().getNewQuantity())
        );
    }

    @Test
    // from no remaining    |   hasn't the crypto   ---> Maybe it's easier to update FROM with the new platform and quantity
    void shouldTransferToPlatformWithoutExistingCryptoAndNoRemaining() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(1.25),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        var fromPlatform = Platform.builder()
                .id("PLTFRM123")
                .name(transferCryptoRequest.getFromPlatform())
                .build();
        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();
        var fromPlatformCrypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(1.25))
                .build();

        var networkFee = transferCryptoRequest.getNetworkFee();
        var quantityToTransfer = transferCryptoRequest.getQuantityToTransfer();
        var totalToAdd = quantityToTransfer.subtract(networkFee);
        var actualCryptoQuantity = fromPlatformCrypto.getQuantity();
        var totalToSubtract = networkFee.add(quantityToTransfer);
        var remainingCryptoQuantity = actualCryptoQuantity.subtract(totalToSubtract);
        var quantityToSendReceive = transferCryptoRequest.getQuantityToTransfer().subtract(networkFee);

        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.of(fromPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), fromPlatform.getId()))
                .thenReturn(Optional.of(fromPlatformCrypto));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), toPlatform.getId()))
                .thenReturn(Optional.empty());

        var transferCryptoResponse = transferCryptoService.transferCrypto(transferCryptoRequest);

        fromPlatformCrypto.setQuantity(remainingCryptoQuantity);
        fromPlatformCrypto.setPlatformId("");

        verify(cryptoRepositoryMock, times(1))
                .save(fromPlatformCrypto);

        assertAll(
                () -> assertEquals(transferCryptoRequest.getNetworkFee(), transferCryptoResponse.getFromPlatform().getNetworkFee()),
                () -> assertEquals(transferCryptoRequest.getQuantityToTransfer(), transferCryptoResponse.getFromPlatform().getQuantityToTransfer()),
                () -> assertEquals(BigDecimal.valueOf(1.25), transferCryptoResponse.getFromPlatform().getTotalToSubtract()),
                () -> assertEquals(quantityToSendReceive, transferCryptoResponse.getFromPlatform().getQuantityToSendReceive()),
                () -> assertEquals(BigDecimal.ZERO, transferCryptoResponse.getFromPlatform().getRemainingCryptoQuantity()),
                () -> assertEquals(totalToAdd, transferCryptoResponse.getToPlatform().getNewQuantity())
        );
    }

    @Test
    void shouldThrowPlatformNotFoundExceptionForUnknownToPlatform() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(1.25),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.empty());

        var exception = assertThrows(PlatformNotFoundException.class,
                () -> transferCryptoService.transferCrypto(transferCryptoRequest));

        assertEquals(TARGET_PLATFORM_NOT_EXISTS, exception.getErrorMessage());
    }

    @Test
    void shouldThrowPlatformNotFoundExceptionForUnknownFromPlatform() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(1.25),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );
        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();

        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.empty());

        var exception = assertThrows(PlatformNotFoundException.class,
                () -> transferCryptoService.transferCrypto(transferCryptoRequest));

        assertEquals(ORIGIN_PLATFORM_NOT_EXISTS, exception.getErrorMessage());
    }

    @Test
    void shouldThrowExceptionIfFromPlatformAndToPlatformAreTheSame() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(1.25),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();

        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.of(toPlatform));

        var exception = assertThrows(ApiValidationException.class,
                () -> transferCryptoService.transferCrypto(transferCryptoRequest));

        assertEquals(SAME_FROM_TO_PLATFORM, exception.getErrorMessage());
    }

    @Test
    void shouldThrowCoinNotFoundExceptionIfCoinIsNotInFromPlatform() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(0.5),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        var fromPlatform = Platform.builder()
                .id("PLTFRM123")
                .name(transferCryptoRequest.getFromPlatform())
                .build();
        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();

        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.of(fromPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(
                transferCryptoRequest.getCryptoId(),
                fromPlatform.getId()
        )).thenReturn(Optional.empty());

        var exception = assertThrows(CoinNotFoundException.class,
                () -> transferCryptoService.transferCrypto(transferCryptoRequest));
        var message = String.format(CRYPTO_NOT_FOUND_IN_PLATFORM, fromPlatform.getName());

        assertEquals(message, exception.getErrorMessage());
    }

    @Test
    void shouldThrowInsufficientBalanceExceptionIfQuantityToTransferIsHigherThanActualQuantity() {
        var transferCryptoRequest = new TransferCryptoRequest(
                "bitcoin",
                BigDecimal.valueOf(15),
                BigDecimal.valueOf(0.001),
                "BINANCE",
                "SAFEPAL"
        );

        var fromPlatform = Platform.builder()
                .id("PLTFRM123")
                .name(transferCryptoRequest.getFromPlatform())
                .build();
        var toPlatform = Platform.builder()
                .id("PLTFRM456")
                .name(transferCryptoRequest.getToPlatform())
                .build();
        var fromPlatformCrypto = Crypto.builder()
                .name("Bitcoin")
                .quantity(BigDecimal.valueOf(1.25))
                .build();

        when(platformRepositoryMock.findByName(transferCryptoRequest.getFromPlatform()))
                .thenReturn(Optional.of(fromPlatform));
        when(platformRepositoryMock.findByName(transferCryptoRequest.getToPlatform()))
                .thenReturn(Optional.of(toPlatform));
        when(cryptoRepositoryMock.findByCoinIdAndPlatformId(transferCryptoRequest.getCryptoId(), fromPlatform.getId()))
                .thenReturn(Optional.of(fromPlatformCrypto));

        var exception = assertThrows(InsufficientBalanceException.class,
                () -> transferCryptoService.transferCrypto(transferCryptoRequest));

        assertEquals(NOT_ENOUGH_BALANCE, exception.getErrorMessage());
    }
}