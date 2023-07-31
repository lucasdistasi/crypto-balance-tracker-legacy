package com.distasilucas.cryptobalancetracker.mapper.impl.crypto;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.entity.UserCrypto;
import com.distasilucas.cryptobalancetracker.exception.CryptoNotFoundException;
import com.distasilucas.cryptobalancetracker.exception.PlatformNotFoundException;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.UserCryptoResponse;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.service.PlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.CRYPTO_NOT_FOUND;
import static com.distasilucas.cryptobalancetracker.constant.ExceptionConstants.PLATFORM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCryptoResponseMapperImpl implements EntityMapper<UserCryptoResponse, UserCrypto> {

    private final CryptoService cryptoService;
    private final PlatformService platformService;

    @Override
    public UserCryptoResponse mapFrom(UserCrypto input) {
        log.info("Mapping UserCrypto with id {}", input.getId());
        Function<UserCrypto, UserCryptoResponse> cryptoResponse = this::getCryptoResponse;

        return cryptoResponse.apply(input);
    }

    private UserCryptoResponse getCryptoResponse(UserCrypto userCrypto) {
        Platform platform = platformService.findById(userCrypto.getPlatformId())
                .orElseThrow(() -> new PlatformNotFoundException(PLATFORM_NOT_FOUND));
        Crypto crypto = cryptoService.findById(userCrypto.getCryptoId())
                .orElseThrow(() -> new CryptoNotFoundException(CRYPTO_NOT_FOUND));

        return UserCryptoResponse.builder()
                .id(userCrypto.getId())
                .cryptoName(crypto.getName())
                .quantity(userCrypto.getQuantity())
                .platform(platform.getName())
                .build();
    }
}
