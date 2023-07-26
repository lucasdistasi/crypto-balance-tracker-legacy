package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.UserCryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoPlatformValidator<T extends CryptoRequest> implements EntityValidation<T> {

    private final CoingeckoService coingeckoService;
    private final PlatformRepository platformRepository;
    private final UserCryptoRepository userCryptoRepository;

    @Override
    public void validate(T cryptoRequest) {
        String platformName = cryptoRequest.getPlatform().toUpperCase();
        Optional<Platform> optionalPlatform = platformRepository.findByName(platformName);

        if (optionalPlatform.isEmpty()) {
            log.info("Platform {} does not exists", cryptoRequest.getPlatform());
            String message = String.format(PLATFORM_NOT_FOUND, cryptoRequest.getPlatform());

            throw new PlatformNotFoundException(message);
        }

        Platform platform = optionalPlatform.get();

        if (cryptoRequest instanceof AddCryptoRequest addCryptoRequest) {
            Coin coin = coingeckoService.retrieveAllCoins()
                    .stream()
                    .filter(c -> c.getName().equalsIgnoreCase(addCryptoRequest.getCryptoName()))
                    .findFirst()
                    .orElseThrow(() -> {
                        String message = String.format(CRYPTO_NAME_NOT_FOUND, addCryptoRequest.getCryptoName());

                        return new CryptoNotFoundException(message);
                    });

            Optional<UserCrypto> optionalCrypto = userCryptoRepository.findByCryptoIdAndPlatformId(coin.getId(), platform.getId());

            optionalCrypto.ifPresent(crypto -> {
                String message = String.format(DUPLICATED_PLATFORM_COIN, coin.getName(), platform.getName());

                throw new ApiValidationException(message);
            });
        }

        if (cryptoRequest instanceof UpdateCryptoRequest updateCryptoRequest) {
            String cryptoId = updateCryptoRequest.getCryptoId();
            Optional<UserCrypto> currentCrypto = userCryptoRepository.findById(cryptoId);

            if (currentCrypto.isEmpty()) {
                String message = String.format(CRYPTO_ID_NOT_FOUND, cryptoId);

                throw new CryptoNotFoundException(message);
            }

            if (!isSamePlatform(currentCrypto.get(), updateCryptoRequest, platform)) {
                Optional<UserCrypto> optionalUserCrypto = userCryptoRepository.findByCryptoIdAndPlatformId(currentCrypto.get().getCryptoId(), platform.getId());

                if (optionalUserCrypto.isPresent()) {
                    String message = String.format(DUPLICATED_PLATFORM_COIN, platform.getName());

                    throw new ApiValidationException(message);
                }
            }
        }
    }

    private boolean isSamePlatform(UserCrypto crypto, UpdateCryptoRequest updateCryptoRequest, Platform platform) {
        return crypto.getCryptoId().equals(updateCryptoRequest.getCryptoId()) && crypto.getPlatformId().equals(platform.getId());
    }
}
