package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.CoingeckoCrypto;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM_CRYPTO;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoPlatformValidator<T extends CryptoRequest> implements EntityValidation<T> {

    private final CoingeckoService coingeckoService;
    private final PlatformService platformService;
    private final UserCryptoRepository userCryptoRepository;

    @Override
    public void validate(T cryptoRequest) {
        String platformName = cryptoRequest.getPlatform().toUpperCase();
        Optional<Platform> requestOptionalPlatform = platformService.findByName(platformName);

        if (requestOptionalPlatform.isEmpty()) {
            log.info("Platform {} does not exists", cryptoRequest.getPlatform());
            String message = String.format(PLATFORM_NOT_FOUND, cryptoRequest.getPlatform());

            throw new PlatformNotFoundException(message);
        }

        Platform requestPlatform = requestOptionalPlatform.get();

        if (cryptoRequest instanceof AddCryptoRequest addCryptoRequest) {
            CoingeckoCrypto coingeckoCrypto = coingeckoService.retrieveAllCoingeckoCryptos()
                    .stream()
                    .filter(c -> c.getName().equalsIgnoreCase(addCryptoRequest.getCryptoName()))
                    .findFirst()
                    .orElseThrow(() -> {
                        String message = String.format(CRYPTO_NAME_NOT_FOUND, addCryptoRequest.getCryptoName());

                        return new CryptoNotFoundException(message);
                    });

            Optional<UserCrypto> optionalCrypto = userCryptoRepository.findByCryptoIdAndPlatformId(coingeckoCrypto.getId(), requestPlatform.getId());

            optionalCrypto.ifPresent(crypto -> {
                String message = String.format(DUPLICATED_PLATFORM_CRYPTO, requestPlatform.getName());

                throw new ApiValidationException(message);
            });
        }

        if (cryptoRequest instanceof UpdateCryptoRequest updateCryptoRequest) {
            String cryptoId = updateCryptoRequest.getCryptoId();
            Optional<UserCrypto> optionalRequestCrypto = userCryptoRepository.findById(cryptoId);

            if (optionalRequestCrypto.isEmpty()) {
                String message = String.format(CRYPTO_ID_NOT_FOUND, cryptoId);

                throw new CryptoNotFoundException(message);
            }

            if (didChangePlatform(optionalRequestCrypto.get(), requestPlatform)) {
                Optional<UserCrypto> optionalUserCrypto = userCryptoRepository.findByCryptoIdAndPlatformId(optionalRequestCrypto.get().getCryptoId(), requestPlatform.getId());

                if (optionalUserCrypto.isPresent()) {
                    String message = String.format(DUPLICATED_PLATFORM_CRYPTO, requestPlatform.getName());

                    throw new ApiValidationException(message);
                }
            }
        }
    }

    private boolean didChangePlatform(UserCrypto requestCrypto, Platform requestPlatform) {
        return !requestCrypto.getPlatformId().equals(requestPlatform.getId());
    }
}
