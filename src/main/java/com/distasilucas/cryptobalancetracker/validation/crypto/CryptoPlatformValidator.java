package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.CoinNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.coingecko.Coin;
import com.distasilucas.cryptobalancetracker.model.request.crypto.AddCryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.CryptoRequest;
import com.distasilucas.cryptobalancetracker.model.request.crypto.UpdateCryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.service.coingecko.CoingeckoService;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_ID_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.COIN_NAME_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoPlatformValidator<T extends CryptoRequest> implements EntityValidation<T> {

    private final CoingeckoService coingeckoService;
    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;

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
                    .filter(c -> c.getName().equalsIgnoreCase(addCryptoRequest.getCoinName()))
                    .findFirst()
                    .orElseThrow(() -> {
                        String message = String.format(COIN_NAME_NOT_FOUND, addCryptoRequest.getCoinName());

                        return new CoinNotFoundException(message);
                    });

            Optional<Crypto> optionalCrypto = cryptoRepository.findByNameAndPlatformId(coin.getName(), platform.getId());

            optionalCrypto.ifPresent(crypto -> {
                String message = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

                throw new ApiValidationException(message);
            });
        }

        if (cryptoRequest instanceof UpdateCryptoRequest updateCryptoRequest) {
            String cryptoId = updateCryptoRequest.getCryptoId();
            Optional<Crypto> currentCrypto = cryptoRepository.findById(cryptoId);

            if (currentCrypto.isEmpty()) {
                String message = String.format(COIN_ID_NOT_FOUND, cryptoId);

                throw new CoinNotFoundException(message);
            }

            if (!isSamePlatform(currentCrypto.get(), updateCryptoRequest, platform)) {
                Optional<Crypto> optionalCrypto = cryptoRepository.findByNameAndPlatformId(currentCrypto.get().getName(), platform.getId());

                optionalCrypto.ifPresent(crypto -> {
                    String message = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

                    throw new ApiValidationException(message);
                });
            }
        }
    }

    private boolean isSamePlatform(Crypto crypto, UpdateCryptoRequest updateCryptoRequest, Platform platform) {
        return crypto.getId().equals(updateCryptoRequest.getCryptoId()) && crypto.getPlatformId().equals(platform.getId());
    }
}
