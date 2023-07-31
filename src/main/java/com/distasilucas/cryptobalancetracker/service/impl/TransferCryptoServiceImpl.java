package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.InsufficientBalanceException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.request.crypto.FromPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.ToPlatform;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.TransferCryptoResponse;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.TransferCryptoService;
import com.distasilucas.cryptobalancetracker.service.UserCryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.NOT_ENOUGH_BALANCE;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.SAME_FROM_TO_PLATFORM;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.TARGET_PLATFORM_NOT_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferCryptoServiceImpl implements TransferCryptoService {

    private final UserCryptoService userCryptoService;
    private final PlatformService platformService;
    private final Validation<TransferCryptoRequest> transferCryptoValidation;

    @Override
    public TransferCryptoResponse transferCrypto(TransferCryptoRequest transferCryptoRequest) {
        transferCryptoValidation.validate(transferCryptoRequest);

        String toPlatformName = transferCryptoRequest.getToPlatform().toUpperCase();
        Platform toPlatform = getToPlatform(toPlatformName);
        String id = transferCryptoRequest.getCryptoId();
        UserCrypto cryptoToTransfer = getCryptoToTransfer(id);

        if (isToAndFromSame(toPlatform.getId(), cryptoToTransfer.getPlatformId()))
            throw new ApiValidationException(SAME_FROM_TO_PLATFORM);

        BigDecimal actualCryptoQuantity = cryptoToTransfer.getQuantity();
        BigDecimal networkFee = transferCryptoRequest.getNetworkFee();
        BigDecimal quantityToTransfer = transferCryptoRequest.getQuantityToTransfer();
        BigDecimal totalToSubtract = getTotalToSubtract(actualCryptoQuantity, quantityToTransfer, networkFee);
        BigDecimal quantityToSendReceive = quantityToTransfer.subtract(networkFee);

        if (hasInsufficientBalance(actualCryptoQuantity, quantityToTransfer))
            throw new InsufficientBalanceException(NOT_ENOUGH_BALANCE);

        Optional<UserCrypto> toPlatformOptionalCrypto = getToPlatformOptionalCrypto(cryptoToTransfer.getCryptoId(), toPlatform);
        BigDecimal remainingCryptoQuantity = getRemainingCryptoQuantity(actualCryptoQuantity, totalToSubtract);
        ToPlatform to = new ToPlatform();
        FromPlatform from = new FromPlatform();

        if (doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isPresent()) {
            UserCrypto toPlatformCrypto = toPlatformOptionalCrypto.get();
            BigDecimal newQuantity = toPlatformCrypto.getQuantity().add(quantityToSendReceive);
            toPlatformCrypto.setQuantity(newQuantity);
            cryptoToTransfer.setQuantity(remainingCryptoQuantity);

            userCryptoService.saveAll(Arrays.asList(toPlatformCrypto, cryptoToTransfer));

            to = new ToPlatform(newQuantity);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        if (doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isEmpty()) {
            UserCrypto cryptoToSave = mapCrypto().apply(cryptoToTransfer);
            cryptoToSave.setQuantity(quantityToSendReceive);
            cryptoToSave.setPlatformId(toPlatform.getId());
            cryptoToTransfer.setQuantity(remainingCryptoQuantity);

            userCryptoService.saveAll(Arrays.asList(cryptoToTransfer, cryptoToSave));

            to = new ToPlatform(quantityToSendReceive);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        if (!doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isPresent()) {
            UserCrypto toPlatformCrypto = toPlatformOptionalCrypto.get();
            BigDecimal newQuantity = toPlatformCrypto.getQuantity().add(quantityToSendReceive);
            toPlatformCrypto.setQuantity(newQuantity);

            userCryptoService.deleteUserCrypto(cryptoToTransfer);
            userCryptoService.saveUserCrypto(toPlatformCrypto);

            to = new ToPlatform(newQuantity);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        if (!doesFromPlatformHasRemaining(remainingCryptoQuantity) && toPlatformOptionalCrypto.isEmpty()) {
            cryptoToTransfer.setQuantity(quantityToSendReceive);
            cryptoToTransfer.setPlatformId(toPlatform.getId());
            userCryptoService.saveUserCrypto(cryptoToTransfer);

            to = new ToPlatform(quantityToSendReceive);
            from = new FromPlatform(networkFee, quantityToTransfer, totalToSubtract, quantityToSendReceive, remainingCryptoQuantity);
        }

        log.info("Transferred {} {} from to {}", quantityToSendReceive, cryptoToTransfer.getCryptoId(), toPlatform.getName());

        return new TransferCryptoResponse(from, to);
    }

    private Platform getToPlatform(String toPlatformName) {
        return platformService.findByName(toPlatformName)
                .orElseThrow(() -> new PlatformNotFoundException(TARGET_PLATFORM_NOT_EXISTS));
    }

    private UserCrypto getCryptoToTransfer(String id) {
        return userCryptoService.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));
    }

    private Optional<UserCrypto> getToPlatformOptionalCrypto(String cryptoId, Platform toPlatform) {
        return userCryptoService.findByCryptoIdAndPlatformId(cryptoId, toPlatform.getId());
    }

    private boolean isToAndFromSame(String toPlatformId, String fromPlatformId) {
        return toPlatformId.equals(fromPlatformId);
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

    private Function<UserCrypto, UserCrypto> mapCrypto() {
        return userCrypto -> UserCrypto.builder()
                .quantity(userCrypto.getQuantity())
                .cryptoId(userCrypto.getCryptoId())
                .platformId(userCrypto.getPlatformId())
                .build();
    }
}
