package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.entity.Platform;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
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
public class CryptoDTOMapperImpl implements EntityMapper<CryptoDTO, Crypto> {

    private final PlatformRepository platformRepository;

    @Override
    public CryptoDTO mapFrom(Crypto input) {
        Function<Crypto, CryptoDTO> cryptoResponse = this::getCryptoDTO;

        return cryptoResponse.apply(input);
    }

    private CryptoDTO getCryptoDTO(Crypto crypto) {
        Optional<Platform> platform = platformRepository.findById(crypto.getPlatformId());
        String platformName = platform.isPresent() ? platform.get().getName() : UNKNOWN;

        return CryptoDTO.builder()
                .coin_name(crypto.getName())
                .quantity(crypto.getQuantity())
                .platform(platformName)
                .ticker(crypto.getTicker())
                .coinId(crypto.getCoinId())
                .lastKnownPrice(crypto.getLastKnownPrice())
                .build();
    }
}
