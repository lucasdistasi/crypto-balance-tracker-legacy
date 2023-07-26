package com.distasilucas.cryptobalancetracker.mapper.impl.crypto;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserCryptoResponseMapperImpl implements EntityMapper<UserCryptoResponse, UserCrypto> {

    private final CryptoRepository cryptoRepository;
    private final PlatformRepository platformRepository;

    @Override
    public UserCryptoResponse mapFrom(UserCrypto input) {
        Function<UserCrypto, UserCryptoResponse> cryptoResponse = this::getCryptoResponse;

        return cryptoResponse.apply(input);
    }

    private UserCryptoResponse getCryptoResponse(UserCrypto userCrypto) {
        Optional<Platform> platform = platformRepository.findById(userCrypto.getPlatformId());
        String platformName = platform.isPresent() ? platform.get().getName() : UNKNOWN;
        Crypto crypto = cryptoRepository.findById(userCrypto.getCryptoId())
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));

        return UserCryptoResponse.builder()
                .id(userCrypto.getId())
                .cryptoName(crypto.getName())
                .quantity(userCrypto.getQuantity())
                .platform(platformName)
                .build();
    }
}
