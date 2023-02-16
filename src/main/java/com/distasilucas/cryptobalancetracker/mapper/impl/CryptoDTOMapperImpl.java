package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.EntityMapper;
import com.distasilucas.cryptobalancetracker.model.request.CryptoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@Service
public class CryptoDTOMapperImpl implements EntityMapper<CryptoDTO, Crypto> {

    @Override
    public CryptoDTO mapFrom(Crypto input) {
        Function<Crypto, CryptoDTO> cryptoResponse = this::getCryptoDTO;

        return cryptoResponse.apply(input);
    }

    private CryptoDTO getCryptoDTO(Crypto crypto) {
        return CryptoDTO.builder()
                .coin_name(crypto.getName())
                .quantity(crypto.getQuantity())
                .platform(crypto.getPlatform().getName())
                .ticker(crypto.getTicker())
                .coinId(crypto.getCoinId())
                .build();
    }
}
