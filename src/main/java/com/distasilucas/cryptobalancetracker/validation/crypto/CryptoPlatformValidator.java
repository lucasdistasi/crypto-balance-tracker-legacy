package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoRequest;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.DUPLICATED_PLATFORM_COIN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoPlatformValidator implements EntityValidation<CryptoRequest> {

    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;

    @Override
    public void validate(CryptoRequest cryptoRequest) {
        String platformName = cryptoRequest.platform().toUpperCase();
        Optional<Platform> optionalPlatform = platformRepository.findByName(platformName);

        if (optionalPlatform.isEmpty()) {
            log.info("Platform {} does not exists", cryptoRequest.platform());
            String message = String.format(PLATFORM_NOT_FOUND, cryptoRequest.platform());

            throw new PlatformNotFoundException(message);
        }

        Platform platform = optionalPlatform.get();
        Optional<Crypto> optionalCrypto = cryptoRepository.findByNameAndPlatformId(cryptoRequest.coin_name(), platform.getId());

        if (optionalCrypto.isPresent()) {
            Crypto crypto = optionalCrypto.get();

            if (platform.getName().equalsIgnoreCase(cryptoRequest.platform())) {
                String message = String.format(DUPLICATED_PLATFORM_COIN, crypto.getName(), platform.getName());

                throw new ApiValidationException(message);
            }
        }
    }
}
