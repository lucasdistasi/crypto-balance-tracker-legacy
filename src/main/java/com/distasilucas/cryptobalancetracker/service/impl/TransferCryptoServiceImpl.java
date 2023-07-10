package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.InsufficientBalanceException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.FromPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.ToPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.TransferCryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND_IN_PLATFORM;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.NOT_ENOUGH_BALANCE;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.ORIGIN_PLATFORM_NOT_EXISTS;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.SAME_FROM_TO_PLATFORM;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.TARGET_PLATFORM_NOT_EXISTS;

@Service
@RequiredArgsConstructor
public class TransferCryptoServiceImpl implements TransferCryptoService {

    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;
    private final Validation<TransferCryptoRequest> transferCryptoValidation;

    @Override
    public TransferCryptoResponse transferCrypto(TransferCryptoRequest transferCryptoRequest) {
        transferCryptoValidation.validate(transferCryptoRequest);

        String toPlatformName = transferCryptoRequest.getToPlatform().toUpperCase();
        String fromPlatformName = transferCryptoRequest.getFromPlatform().toUpperCase();
        Platform toPlatform = getToPlatform(toPlatformName);
        Platform fromPlatform = getFromPlatform(fromPlatformName);

        if (isToAndFromSame(toPlatform, fromPlatform))
            throw new ApiValidationException(SAME_FROM_TO_PLATFORM);

        Crypto fromPlatformCrypto = getFromPlatformCrypto(transferCryptoRequest, fromPlatform);
        BigDecimal actualCryptoQuantity = fromPlatformCrypto.getQuantity();
        BigDecimal networkFee = transferCryptoRequest.getNetworkFee();
        BigDecimal quantityToTransfer = transferCryptoRequest.getQuantityToTransfer();
        BigDecimal totalToSubtract = getTotalToSubtract(actualCryptoQuantity, quantityToTransfer, networkFee);
        BigDecimal quantityToSendReceive = quantityToTransfer.subtract(networkFee);

        if (hasInsufficientBalance(actualCryptoQuantity, quantityToTransfer))
            throw new InsufficientBalanceException(NOT_ENOUGH_BALANCE);

        Optional<Crypto> toPlatformOptionalCrypto = getToPlatformOptionalCrypto(transferCryptoRequest, toPlatform);
        BigDecimal remainingCryptoQuantity = getRemainingCryptoQuantity(actualCryptoQuantity, totalToSubtract);
        ToPlatform to = new ToPlatform();
        FromPlatform from = new FromPlatform();

        if (doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isPresent()) {
            Crypto toPlatformCrypto = toPlatformOptionalCrypto.get();
            BigDecimal newQuantity = toPlatformCrypto.getQuantity().add(quantityToSendReceive);
            toPlatformCrypto.setQuantity(newQuantity);
            fromPlatformCrypto.setQuantity(remainingCryptoQuantity);

            cryptoRepository.saveAll(Arrays.asList(toPlatformCrypto, fromPlatformCrypto));

            to = new ToPlatform(newQuantity);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        if (doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isEmpty()) {
            Crypto cryptoToSave = mapCrypto().apply(fromPlatformCrypto);
            cryptoToSave.setQuantity(quantityToSendReceive);
            cryptoToSave.setPlatformId(toPlatform.getId());
            fromPlatformCrypto.setQuantity(remainingCryptoQuantity);

            cryptoRepository.saveAll(Arrays.asList(fromPlatformCrypto, cryptoToSave));

            to = new ToPlatform(quantityToSendReceive);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        if (!doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isPresent()) {
            Crypto toPlatformCrypto = toPlatformOptionalCrypto.get();
            BigDecimal newQuantity = toPlatformCrypto.getQuantity().add(quantityToSendReceive);
            toPlatformCrypto.setQuantity(newQuantity);

            cryptoRepository.delete(fromPlatformCrypto);
            cryptoRepository.save(toPlatformCrypto);

            to = new ToPlatform(newQuantity);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        if (!doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isEmpty()) {
            fromPlatformCrypto.setQuantity(quantityToSendReceive);
            fromPlatformCrypto.setPlatformId(toPlatform.getId());
            cryptoRepository.save(fromPlatformCrypto);

            to = new ToPlatform(quantityToSendReceive);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        return new TransferCryptoResponse(from, to);
    }

    private Platform getFromPlatform(String fromPlatformName) {
        return platformRepository.findByName(fromPlatformName)
                .orElseThrow(() -> new PlatformNotFoundException(ORIGIN_PLATFORM_NOT_EXISTS));
    }

    private Platform getToPlatform(String toPlatformName) {
        return platformRepository.findByName(toPlatformName)
                .orElseThrow(() -> new PlatformNotFoundException(TARGET_PLATFORM_NOT_EXISTS));
    }

    private Crypto getFromPlatformCrypto(TransferCryptoRequest transferCryptoRequest, Platform fromPlatform) {
        return cryptoRepository.findByCoinIdAndPlatformId(
                transferCryptoRequest.getCryptoId(),
                fromPlatform.getId()
        ).orElseThrow(() -> {
            String message = String.format(CRYPTO_NOT_FOUND_IN_PLATFORM, fromPlatform.getName());

            return new CoinNotFoundException(message);
        });
    }

    private Optional<Crypto> getToPlatformOptionalCrypto(TransferCryptoRequest transferCryptoRequest, Platform toPlatform) {
        return cryptoRepository.findByCoinIdAndPlatformId(
                transferCryptoRequest.getCryptoId(),
                toPlatform.getId()
        );
    }

    private boolean isToAndFromSame(Platform toPlatform, Platform fromPlatform) {
        return toPlatform.getId().equals(fromPlatform.getId());
    }

    private BigDecimal getRemainingCryptoQuantity(BigDecimal actualCryptoQuantity, BigDecimal totalToSubtract) {
        return actualCryptoQuantity.compareTo(totalToSubtract) > 0 ?
                actualCryptoQuantity.subtract(totalToSubtract) :
                BigDecimal.ZERO;
    }

    private BigDecimal getTotalToSubtract(BigDecimal actualCryptoQuantity, BigDecimal quantityToTransfer, BigDecimal networkFee) {
        BigDecimal totalToSpent = quantityToTransfer.add(networkFee);

        return actualCryptoQuantity.compareTo(totalToSpent) > 0 ? totalToSpent : actualCryptoQuantity;
    }

    private boolean hasInsufficientBalance(BigDecimal actualCryptoQuantity, BigDecimal totalToSubtract) {
        return totalToSubtract.compareTo(actualCryptoQuantity) > 0;
    }

    private boolean doesFromPlatformHasRemaining(BigDecimal remainingCryptoQuantity) {
        return remainingCryptoQuantity.compareTo(BigDecimal.ZERO) > 0;
    }

    private Function<Crypto, Crypto> mapCrypto() {
        return crypto -> Crypto.builder()
                .name(crypto.getName())
                .ticker(crypto.getTicker())
                .coinId(crypto.getCoinId())
                .lastKnownPrice(crypto.getLastKnownPrice())
                .lastKnownPriceInEUR(crypto.getLastKnownPriceInEUR())
                .lastKnownPriceInBTC(crypto.getLastKnownPriceInBTC())
                .circulatingSupply(crypto.getCirculatingSupply())
                .maxSupply(crypto.getMaxSupply())
                .lastPriceUpdatedAt(crypto.getLastPriceUpdatedAt())
                .build();
    }
}
