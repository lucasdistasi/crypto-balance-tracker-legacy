package com.distasilucas.cryptobalancetracker.mapper.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.mapper.Mapper;
import com.distasilucas.cryptobalancetracker.model.CryptoDTO;
import org.springframework.stereotype.Service;

@Service
public class CryptoMapper implements Mapper<Crypto, CryptoDTO> {

    @Override
    public Crypto mapTo(CryptoDTO cryptoDTO) {
        return null;
    }
}
