package com.distasilucas.cryptobalancetracker.service.impl;

import com.distasilucas.cryptobalancetracker.entity.Crypto;
import com.distasilucas.cryptobalancetracker.model.CryptoDTO;
import com.distasilucas.cryptobalancetracker.repository.CryptoRepository;
import com.distasilucas.cryptobalancetracker.service.CryptoService;
import com.distasilucas.cryptobalancetracker.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService<Crypto, CryptoDTO> {

    private final CryptoRepository cryptoRepository;
    private final Validation<CryptoDTO> addCryptoValidation;

    @Override
    public Crypto add(CryptoDTO cryptoDTO) {
        log.info("Saving Crypto {}", cryptoDTO);

        addCryptoValidation.validate(cryptoDTO);
        Crypto crypto = Crypto.builder()
                .ticker(cryptoDTO.getTicker())
                .name("name")
                .quantity(cryptoDTO.getQuantity())
                .build();

        return cryptoRepository.save(crypto);
    }
}
