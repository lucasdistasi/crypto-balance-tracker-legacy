package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.response.crypto.CryptoResponse;
import com.distasilucas.cryptobalancetracker.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

import static com.distasilucas.cryptobalancetracker.constant.Constants.UNKNOWN;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoResponseMapperImpl implements EntityMapper<CryptoResponse, Crypto> {

    private final PlatformRepository platformRepository;

    @Override
    public CryptoResponse mapFrom(Crypto input) {
        Function<Crypto, CryptoResponse> cryptoResponse = this::getCryptoResponse;

        return cryptoResponse.apply(input);
    }

    private CryptoResponse getCryptoResponse(Crypto crypto) {
        Optional<Platform> platform = platformRepository.findById(crypto.getPlatformId());
        String platformName = platform.isPresent() ? platform.get().getName() : UNKNOWN;

        return CryptoResponse.builder()
                .coinId(crypto.getId())
                .coinName(crypto.getName())
                .quantity(crypto.getQuantity())
                .platform(platformName)
                .build();
    }
}
