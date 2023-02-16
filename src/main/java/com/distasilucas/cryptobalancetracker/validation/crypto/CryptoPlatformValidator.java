package com.distasilucas.cryptobalancetracker.validation.crypto;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.exception.ApiValidationException;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.validation.EntityValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.distasilucas.cryptobalancetracker.constant.Constants.DUPLICATED_PLATFORM_COIN;

@Service
@RequiredArgsConstructor
public class CryptoPlatformValidator implements EntityValidation<CryptoDTO> {

    private final CryptoRepository cryptoRepository;

    @Override
    public void validate(CryptoDTO cryptoDTO) {
        Optional<Crypto> optionalCrypto = cryptoRepository.findByName(cryptoDTO.coin_name());

        if (optionalCrypto.isPresent()) {
            Crypto crypto = optionalCrypto.get();
            String platformName = crypto.getPlatform().getName();
            String coinName = crypto.getName();

            if (platformName.equalsIgnoreCase(cryptoDTO.platform())) {
                String message = String.format(DUPLICATED_PLATFORM_COIN, coinName, platformName);

                throw new ApiValidationException(message);
            }
        }

    }
}
